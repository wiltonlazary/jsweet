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

import static org.junit.Assert.fail;

import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.util.EvaluationResult;
import org.junit.Assert;
import org.junit.Test;

import source.calculus.Chars;
import source.calculus.Integers;
import source.calculus.Longs;
import source.calculus.MathApi;
import source.calculus.Null;
import source.calculus.Numbers;
import source.calculus.Operators;

public class CalculusTests extends AbstractTest {

	@Test
	public void testIntegers() {
		try {
			TestTranspilationHandler logHandler = new TestTranspilationHandler();
			EvaluationResult r = transpiler.eval("Java", logHandler, getSourceFile(Integers.class));
			logHandler.assertReportedProblems();
			Assert.assertEquals("3", r.get("i").toString());
			Assert.assertEquals((Integer) 1, r.get("i1"));
			Assert.assertEquals((Integer) 1, r.get("i2"));
			Assert.assertEquals((Double) 1.5, r.get("f1"));
			Assert.assertEquals((Double) 1.5, r.get("f2"));
			Assert.assertEquals((Double) 7.5, r.get("f3"));
			Assert.assertEquals((Integer) 7, r.get("i3"));
			Assert.assertEquals((Integer) 7, r.get("i4"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured while running test");
		}
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			Assert.assertEquals("3", r.get("i").toString());
			Assert.assertEquals((Integer) 1, r.get("i1"));
			Assert.assertEquals((Integer) 1, r.get("i2"));
			Assert.assertEquals((Double) 1.5, r.get("f1"));
			Assert.assertEquals((Double) 1.5, r.get("f2"));
			Assert.assertEquals((Double) 7.5, r.get("f3"));
			Assert.assertEquals((Integer) 7, r.get("i3"));
			Assert.assertEquals((Integer) 7, r.get("i4"));
		}, getSourceFile(Integers.class));
	}

	@Test
	public void testLongs() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			Assert.assertEquals(r.get("t1").toString(), r.get("t2").toString());
		}, getSourceFile(Longs.class));
	}

	@Test
	public void testMathApi() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();

			Assert.assertEquals(Math.E, (double) r.get("E"), 0.00001);
			Assert.assertEquals(Math.PI, (double) r.get("PI"), 0.00001);
			Assert.assertEquals(Math.abs(-123), (int) r.get("abs_123"), 0.00001);
			Assert.assertEquals(Math.acos(0.1), (double) r.get("acos0_1"), 0.00001);
			Assert.assertEquals(Math.floor(3.3), (int) r.get("floor3_3"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("cbrt2"), 0.00001);
			Assert.assertEquals(Math.signum(-2342), (int) r.get("signum_2342"), 0.00001);
			Assert.assertEquals(Math.scalb(1.2, 2), (double) r.get("scalb1_2__2"), 0.00001);
			Assert.assertEquals(Math.toDegrees(0.5), (double) r.get("toDegres0_5"), 0.00001);
			Assert.assertEquals(Math.abs(-1) + Math.abs(-1), (int) r.get("2"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("3"), 0.00001);
			Assert.assertEquals(Math.cbrt(2), (double) r.get("4"), 0.00001);
		}, getSourceFile(MathApi.class));
	}

	@Test
	public void testOperators() {
		transpile(ModuleKind.none, (logHandler) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(Operators.class));
	}

	@Test
	public void testChars() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			Assert.assertEquals("" + (char) (0x0030 + ((int) (5 * 10))), r.get("result"));
		}, getSourceFile(Chars.class));
	}

	@Test
	public void testNull() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
		}, getSourceFile(Null.class));
	}

	@Test
	public void testNumbers() {
		eval(ModuleKind.none, (logHandler, r) -> {
			logHandler.assertReportedProblems();
			Assert.assertTrue(r.get("NaN_test"));
		}, getSourceFile(Numbers.class));
	}

}
