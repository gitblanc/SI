/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.io.database;

import java.io.IOException;

public interface CaseDatabaseWriter {
	void save(String filename, CaseDatabase database) throws IOException;
}
