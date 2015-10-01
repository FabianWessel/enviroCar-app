package org.envirocar.app;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.envirocar.app.activity.DialogUtil;
import org.envirocar.app.application.TermsOfUseManager;
import org.envirocar.app.application.UploadManager;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.bluetooth.BluetoothHandler;
import org.envirocar.app.bluetooth.service.BluetoothServiceState;
import org.envirocar.app.events.bluetooth.BluetoothServiceStateChangedEvent;
import org.envirocar.app.exception.ServerException;
import org.envirocar.app.injection.InjectApplicationScope;
import org.envirocar.app.injection.Injector;
import org.envirocar.app.logging.Logger;
import org.envirocar.app.model.TermsOfUseInstance;
import org.envirocar.app.model.TrackId;
import org.envirocar.app.model.User;
import org.envirocar.app.model.dao.DAOProvider;
import org.envirocar.app.model.dao.exception.NotConnectedException;
import org.envirocar.app.model.dao.exception.UnauthorizedException;
import org.envirocar.app.storage.DbAdapter;
import org.envirocar.app.storage.RemoteTrack;
import org.envirocar.app.storage.Track;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author de Wall
 */
public class TrackHandler {
    private static final Logger LOGGER = Logger.getLogger(TrackHandler.class);
    private static final String TRACK_MODE = "trackMode";

    /**
     * Callback interface for uploading a track.
     */
    public interface TrackUploadCallback {

        void onUploadStarted(Track track);

        /**
         * Called if the track has been successfully uploaded.
         *
         * @param track the track to upload.
         */
        void onSuccessfulUpload(Track track);

        /**
         * Called if an error occured during the upload routine.
         *
         * @param track   the track that was intended to be uploaded.
         * @param message the error message to be displayed within snackbar.
         */
        void onError(Track track, String message);
    }

    @Inject
    @InjectApplicationScope
    protected Context mContext;
    @Inject
    protected Bus mBus;
    @Inject
    protected DbAdapter mDBAdapter;
    @Inject
    protected BluetoothHandler mBluetoothHandler;
    @Inject
    protected DAOProvider mDAOProvider;
    @Inject
    protected UserManager mUserManager;
    @Inject
    protected TermsOfUseManager mTermsOfUseManager;

    private Scheduler.Worker mBackgroundWorker = Schedulers.io().createWorker();

    private BluetoothServiceState mBluetoothServiceState = BluetoothServiceState.SERVICE_STOPPED;


    /**
     * Constructor.
     *
     * @param context the context of the activity's scope.
     */
    public TrackHandler(Context context) {
        // Inject all annotated fields.
        ((Injector) context).injectObjects(this);
    }

    /**
     * Deletes a track and returns true if the track has been successfully deleted.
     *
     * @param trackID the id of the track to delete.
     * @return true if the track has been successfully deleted.
     */
    public boolean deleteLocalTrack(TrackId trackID) {
        Track dbRefTrack = mDBAdapter.getTrack(trackID, true);
        return deleteLocalTrack(dbRefTrack);
    }

    /**
     * Deletes a track and returns true if the track has been successfully deleted.
     *
     * @param trackRef the reference of the track.
     * @return true if the track has been successfully deleted.
     */
    public boolean deleteLocalTrack(Track trackRef) {
        LOGGER.info(String.format("deleteLocalTrack(id = %s)", trackRef.getTrackId().getId()));

        // Only delete the track if the track is a local track.
        if (trackRef.isLocalTrack()) {
            LOGGER.info("deleteLocalTrack(...): Track is a local track.");
            mDBAdapter.deleteTrack(trackRef.getTrackId());
            return true;
        }

        LOGGER.warn("deleteLocalTrack(...): track is no local track. No deletion.");
        return false;
    }

    /**
     * Invokes the deletion of a remote track. Once the remote track has been successfully
     * deleted, this method also deletes the locally stored reference of that track.
     *
     * @param trackRef
     * @return
     * @throws UnauthorizedException
     * @throws NotConnectedException
     */
    public boolean deleteRemoteTrack(Track trackRef) throws UnauthorizedException,
            NotConnectedException {
        LOGGER.info(String.format("deleteRemoteTrack(id = %s)", trackRef.getTrackId().getId()));

        // Check whether this track is a remote track.
        if (!trackRef.isRemoteTrack()) {
            LOGGER.warn("Track reference to upload is no remote track.");
            return false;
        }

        // Delete the track first remote and then the local reference.
        mDAOProvider.getTrackDAO().deleteTrack(((RemoteTrack) trackRef).getRemoteID());
        mDBAdapter.deleteTrack(trackRef.getTrackId());

        // Successfully deleted the remote track.
        LOGGER.info("deleteRemoteTrack(): Successfully deleted the remote track.");
        return true;
    }

    public Track getTrackByID(long trackId) {
        return getTrackByID(new TrackId(trackId));
    }

    /**
     *
     */
    public Track getTrackByID(TrackId trackId) {
        LOGGER.info(String.format("getTrackByID(%s)", trackId.toString()));
        return mDBAdapter.getTrack(trackId);
    }

    // TODO REMOVE THIS ACTIVITY STUFF... unbelievable.. no structure!
    public void uploadTrack(Activity activity, Track track, TrackUploadCallback callback) {
        // If the track is no local track, then popup a snackbar.
        if (!track.isLocalTrack()) {
            String infoText = String.format(mContext.getString(R.string
                    .trackviews_is_already_uploaded), track.getName());
            LOGGER.info(infoText);
            callback.onError(track, infoText);
            return;
        }

        // If the user is not logged in, then skip the upload and popup a snackbar.
        if (!mUserManager.isLoggedIn()) {
            LOGGER.warn("Cannot upload track, because the user is not logged in");
            String infoText = mContext.getString(R.string.trackviews_not_logged_in);
            callback.onError(track, infoText);
            return;
        }

        // First, try to get whether the user has accepted the terms of use.
        final User user = mUserManager.getUser();
        boolean verified = false;
        try {
            verified = mTermsOfUseManager.verifyTermsUseOfVersion(user.getTouVersion());
        } catch (ServerException e) {
            LOGGER.warn(e.getMessage(), e);
            String infoText = mContext.getString(R.string.trackviews_server_error);
            callback.onError(track, infoText);
            return;
        }

        // If the user has not accepted the terms of use, then show a dialog where he
        // can accept the terms of use.
        if (!verified) {
            final TermsOfUseInstance current;
            try {
                current = mTermsOfUseManager.getCurrentTermsOfUse();
            } catch (ServerException e) {
                LOGGER.warn("This should never happen!", e);
                callback.onError(track, "Terms Of Use not accepted.");
                return;
            }

            // Create a dialog with which the user can accept the terms of use.
            DialogUtil.createTermsOfUseDialog(current,
                    user.getTouVersion() == null, new DialogUtil
                            .PositiveNegativeCallback() {

                        @Override
                        public void negative() {
                            LOGGER.info("User did not accept the ToU.");
                            callback.onError(track, mContext.getString(R.string
                                    .terms_of_use_info));
                        }

                        @Override
                        public void positive() {
                            // If the user accepted the terms of use, then update this and
                            // finally upload the track.
                            mTermsOfUseManager.userAcceptedTermsOfUse(user, current
                                    .getIssuedDate());
                            new UploadManager(activity).uploadSingleTrack(track, callback);
                        }

                    }, activity);

            return;
        }

        // Upload the track if everything is right.
        new UploadManager(activity).uploadSingleTrack(track, callback);
    }

    public Track downloadTrack(String remoteID) throws NotConnectedException {
        Track downloadedTrack = mDAOProvider.getTrackDAO().getTrack(remoteID);
        mDBAdapter.insertTrack(downloadedTrack);

        return downloadedTrack;
    }

    public Observable<RemoteTrack> fetchRemoteTrackObservable(RemoteTrack remoteTrack) {
        return Observable.just(remoteTrack)
                .map(remoteTrack1 -> {
                    try {
                        return fetchRemoteTrack(remoteTrack1);
                    } catch (NotConnectedException e) {
                        throw OnErrorThrowable.from(e);
                    }
                });
    }

    public RemoteTrack fetchRemoteTrack(RemoteTrack remoteTrack) throws NotConnectedException {
        Track downloadedTrack = mDAOProvider.getTrackDAO().getTrack(remoteTrack.getRemoteID());
        remoteTrack.copyVariables(downloadedTrack);
        mDBAdapter.insertTrack(remoteTrack, true);
        return remoteTrack;
    }

    /**
     * Finishes the current track. On the one hand, the service that handles the connection to
     * the Bluetooth device gets closed and the track in the database gets finished.
     */
    public void finishCurrentTrack() {
        LOGGER.info("stopTrack()");

        // Set the current service state to SERVICE_STOPPING.
        mBus.post(new BluetoothServiceStateChangedEvent(BluetoothServiceState.SERVICE_STOPPING));

        // Schedule a new async task for closing the service, finishing the current track, and
        // finally fireing an event on the event bus.
        mBackgroundWorker.schedule(() -> {
            LOGGER.info("backgroundworker");
            // Stop the background service that is responsible for the OBDConnection.
            mBluetoothHandler.stopOBDConnectionService();

            // Finish the current track.
            final Track track = mDBAdapter.finishCurrentTrack();

            // Fire a new TrackFinishedEvent on the event bus.
            mBus.post(new TrackFinishedEvent(track));
        });
    }

    @Subscribe
    public void onReceiveBluetoothServiceStateChangedEvent(
            BluetoothServiceStateChangedEvent event) {
        LOGGER.info(String.format("onReceiveBluetoothServiceStateChangedEvent: %s",
                event.toString()));
        mBluetoothServiceState = event.mState;
    }

}
