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
package org.envirocar.obd.commands.request;

import org.envirocar.obd.commands.PID;

public class PIDCommand implements BasicCommand {

    private final int expectedResponseLines;
    private String mode;
    private PID pid;
    private byte[] bytes;

    /**
     * @param mode the mode of the PID cmd
     * @param pid the PID to be requested
     *
     */
    public PIDCommand(String mode, PID pid) {
        this(mode, pid, 0);
    }

    /**
     * @param mode the mode of the PID cmd
     * @param pid the PID to be requested
     * @param expectedResponseLines the number of response lines to be expected after execution.
     *                              The OBD adapter will wait for this amount of lines and write
     *                              the response immediately after reception of it. E.g. providing 1
     *                              results in a fast response. Providing 0 will result in the
     *                              default OBD behaviour of waiting 200ms for responses and return
     *                              the first.
     */
    public PIDCommand(String mode, PID pid, int expectedResponseLines) {
        if (expectedResponseLines < 0 || expectedResponseLines > 9) {
            throw new IllegalStateException("expectedResponseLines out of allowed bounds");
        }

        this.mode = mode;
        this.pid = pid;
        this.expectedResponseLines = expectedResponseLines;

        prepareBytes();
    }

    private void prepareBytes() {
        int ml = mode.length();
        String pidString = pid.getHexadecimalRepresentation();
        int pl = pidString.length();
        bytes = new byte[ml + pl + 1 + (expectedResponseLines == 0 ? 0 : 1)];

        int pos = 0;
        for (int i = 0; i < ml; i++) {
            bytes[pos++] = (byte) mode.charAt(i);
        }

        bytes[pos++] = ' ';

        for (int i = 0; i < pl; i++) {
            bytes[pos++] = (byte) pidString.charAt(i);
        }

        if (expectedResponseLines > 0) {
            bytes[pos++] = (byte) Integer.toString(this.expectedResponseLines).charAt(0);
        }
    }

    public String getMode() {
        return mode;
    }

    public PID getPid() {
        return pid;
    }

    @Override
    public byte[] getOutputBytes() {
        return bytes;
    }

    @Override
    public boolean awaitsResults() {
        return true;
    }
}
