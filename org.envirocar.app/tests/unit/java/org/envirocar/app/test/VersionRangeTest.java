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
package org.envirocar.app.test;

public class VersionRangeTest {

//	@Test
//	public void testVersionSorting() {
//
//		List<Version> list = new ArrayList<Version>();
//		list.add(Version.fromString("1.1.0"));
//		list.add(Version.fromString("1.0.0"));
//		list.add(Version.fromString("5000.0.0"));
//		list.add(Version.fromString("1.0.11"));
//		list.add(Version.fromString("0.1.0"));
//		list.add(Version.fromString("0.1.1-SNAPSHOT"));
//		list.add(Version.fromString("0.2.3-SNAPSHOT"));
//		list.add(Version.fromString("0.2.0"));
//
//		Collections.sort(list);
//
//		Assert.assertTrue(list.get(1).equals(Version.fromString("0.1.1-SNAPSHOT")));
//		Assert.assertTrue(list.get(2).equals(Version.fromString("0.2.0")));
//		Assert.assertTrue(list.get(3).equals(Version.fromString("0.2.3-SNAPSHOT")));
//		Assert.assertTrue(list.get(4).equals(Version.fromString("1.0.0")));
//		Assert.assertTrue(list.get(5).equals(Version.fromString("1.0.11")));
//		Assert.assertTrue(list.get(6).equals(Version.fromString("1.1.0")));
//		Assert.assertTrue(list.get(7).equals(Version.fromString("5000.0.0")));
//
//	}
//
//    @Test
//	public void testVersionParsing() {
//		Version v = Version.fromString("23.42.1111-SNAPSHOT");
//
//		Assert.assertEquals(23, v.getMajor());
//		Assert.assertEquals(42, v.getMinor());
//		Assert.assertEquals(1111, v.getFix());
//		Assert.assertEquals(true, v.isSnapshot());
//	}
//
//    @Test
//	public void testRangeParsing() {
//		VersionRange range = VersionRange.fromString("(0, 0.8.0]");
//
//		Assert.assertEquals(Version.fromString("0.8.0"),range.getMaximum());
//		Assert.assertEquals(Version.fromString("0.0.0"),range.getMinimum());
//		Assert.assertEquals(true, range.isMaximumIncluded());
//		Assert.assertEquals(false, range.isMinimumIncluded());
//	}
//
//    @Test
//	public void testInRange() {
//		VersionRange range = VersionRange.fromString("[0.2, 12.3.2)");
//
//		Assert.assertTrue(!range.isInRange(Version.fromString("0.1.9999999")));
//		Assert.assertTrue(range.isInRange(Version.fromString("0.2.0")));
//		Assert.assertTrue(range.isInRange(Version.fromString("0.2.1")));
//		Assert.assertTrue(range.isInRange(Version.fromString("12.3.1")));
//		Assert.assertTrue(!range.isInRange(Version.fromString("12.3.2")));
//	}

}
