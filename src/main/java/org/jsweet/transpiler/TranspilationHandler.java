/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler;

import org.apache.log4j.Logger;

/**
 * Objects implementing this interface handle transpilation errors and warnings.
 * 
 * @author Renaud Pawlak
 */
public interface TranspilationHandler {

	Logger OUTPUT_LOGGER = Logger.getLogger("output");

	/**
	 * This method is called by the transpiler when a problem needs to be
	 * reported.
	 * 
	 * @param problem
	 *            the reported problem
	 * @param sourcePosition
	 *            the position in the source file
	 * @param message
	 *            the reported message
	 */
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message);

	/**
	 * This method is invoked when the tranpilation process ends.
	 * 
	 * @param transpiler
	 *            the transpiler that generates this event
	 * @param fullPass
	 *            true for a full transpilation (i.e. a non-watch mode
	 *            transpilation or first pass of a watch mode), false for an
	 *            incremental transpilation in the watch mode
	 * @param files
	 *            the files that were transpiled (can be different from
	 *            <code>transpiler.getWatchedFiles()</code> in a non-full pass)
	 */
	public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files);

}