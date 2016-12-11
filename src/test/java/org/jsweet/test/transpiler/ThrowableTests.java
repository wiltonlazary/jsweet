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
package org.jsweet.test.transpiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Test;

import source.candies.SocketIOLib;
import source.throwable.InvalidTryCatchTest;
import source.throwable.MultipleTryCatchTest;
import source.throwable.Throwables;
import source.throwable.TryCatchFinallyTest;

public class ThrowableTests extends AbstractTest {

	@Test
	public void testTryCatchFinally() {
		eval(ModuleKind.none, (logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertNotNull("Test was not executed", r.get("executed"));
			Assert.assertEquals("Expected a message when the catch clause is executed", "test-message", r.get("message"));
			Assert.assertNotNull("Finally was not executed", r.get("finally_executed"));
		}, getSourceFile(TryCatchFinallyTest.class));
	}

	@Test
	public void testMultipleTryCatch() {
		eval(ModuleKind.none, (logHandler, r) -> {
			Assert.assertEquals("There should be no errors", 0, logHandler.reportedProblems.size());
			Assert.assertNotNull("Test was not executed", r.get("executed"));
			Assert.assertEquals("Expected a message when the catch clause is executed", "test-message", r.get("message1"));
			Assert.assertNotNull("Finally was not executed", r.get("finally_executed"));
			Assert.assertNull(r.get("message2"));
			Assert.assertNull(r.get("message3"));
		}, getSourceFile(MultipleTryCatchTest.class));
	}

	@Test
	public void testInvalidTryCatch() {
		transpile(ModuleKind.none, logHandler -> {
			logHandler.assertReportedProblems(JSweetProblem.UNSUPPORTED_TRY_WITH_RESOURCE, JSweetProblem.TRY_WITHOUT_CATCH_OR_FINALLY);
		}, getSourceFile(InvalidTryCatchTest.class));
	}

	@Test
	public void testThrowables() {
		eval((handler, result) -> {
			handler.assertReportedProblems();
			Assert.assertEquals(true, result.get("catch1"));
			Assert.assertEquals(true, result.get("catch2"));
			// the message is not passed to the error superclass...
			// it seems to be a well-known problem of current JS engines:
			// https://github.com/Microsoft/TypeScript/issues/5069
			// TODO: should we patch it or wait that it is fixed?
			Assert.assertEquals("message", result.get("message2"));
			Assert.assertEquals(true, result.get("catch3"));
			Assert.assertEquals("message2", result.get("message3"));
			// cause does not work yet and returns the current error
			Assert.assertEquals(true, result.get("cause"));
		}, getSourceFile(Throwables.class));
	}

}
