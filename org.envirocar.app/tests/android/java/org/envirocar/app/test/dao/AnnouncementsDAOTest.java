/**
 * Copyright (C) 2013 - 2021 the enviroCar community
 *
 * This file is part of the enviroCar app.
 *
 * The enviroCar app is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The enviroCar app is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with the enviroCar app. If not, see http://www.gnu.org/licenses/.
 */
///*
// * enviroCar 2013
// * Copyright (C) 2013
// * Martin Dueren, Jakob Moellers, Gerald Pape, Christopher Stephan
// *
// * This program is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation; either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software Foundation,
// * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
// *
// */
//package org.envirocar.app.test.dao;
//
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//import junit.framework.Assert;
//
//import org.envirocar.app.model.dao.DAOProvider;
//import org.envirocar.app.model.dao.cache.CacheAnnouncementsDAO;
//import org.envirocar.app.model.dao.exception.AnnouncementsRetrievalException;
//import org.envirocar.app.model.Announcement;
//import org.envirocar.app.model.Announcement.Priority;
//import org.envirocar.app.util.VersionRange.Version;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class AnnouncementsDAOTest extends CacheDAOTest {
//
//	public void testAnnouncementCache() throws IOException, AnnouncementsRetrievalException {
//		DAOProvider prov = getDAOProvider();
//
//		prepareCache(getMockupDir().getBaseFolder(), "/announcements_mockup.json", CacheAnnouncementsDAO.CACHE_FILE_NAME);
//
//		List<Announcement> all = prov.getAnnouncementsDAO().getAllAnnouncements();
//
//		Assert.assertTrue(all.size() == 1);
//
//		Announcement first = all.get(0);
//
//		Assert.assertTrue(first.getCategory().equals("app"));
//		Assert.assertTrue(first.getId().equals("asdfg12345"));
//		Assert.assertTrue(first.getVersionRange().isInRange(Version.fromString("0.7.0")));
//		Assert.assertTrue(first.getPriority().equals(Priority.MEDIUM));
//		String enContent = first.getContent(Locale.ENGLISH);
//		String itContent = first.getContent(Locale.ITALIAN);
//
//		Assert.assertNotNull(enContent);
//
//		/*
//		 * cause there is no IT locale content
//		 */
//		Assert.assertTrue(enContent.equals(itContent));
//
//		Assert.assertNotNull(first.createUITitle(InstrumentationRegistry.getContext()));
//	}
//
//}
