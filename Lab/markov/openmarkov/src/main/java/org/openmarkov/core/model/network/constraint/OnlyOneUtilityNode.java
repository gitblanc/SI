/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.constraint;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint(name = "OnlyOneUtilityNode", defaultBehavior = ConstraintBehavior.OPTIONAL) public class OnlyOneUtilityNode
		extends PNConstraint {

	@Override public boolean checkProbNet(ProbNet probNet) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override public boolean checkEdit(ProbNet probNet, PNEdit edit) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override protected String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
