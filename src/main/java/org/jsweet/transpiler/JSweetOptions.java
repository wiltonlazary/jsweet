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

import java.io.File;

public interface JSweetOptions {

	/**
	 * Tells if the transpiler preserves the generated TypeScript source line
	 * numbers wrt the Java original source file (allows for Java debugging
	 * through js.map files).
	 */
	boolean isPreserveSourceLineNumbers();

	/**
	 * Gets the current TypeScript output directory.
	 */
	File getTsOutputDir();

	/**
	 * Gets the current JavaScript output directory.
	 */
	File getJsOutputDir();

	/**
	 * Gets the current .d.ts output directory (only if the declaration option
	 * is set).
	 * 
	 * <p>
	 * By default, declarations are placed in the JavaScript output directory.
	 */
	File getDeclarationsOutputDir();

	/**
	 * Gets the module kind when transpiling to code using JavaScript modules.
	 */
	ModuleKind getModuleKind();

	/**
	 * Gets the directory where JavaScript bundles are generated when the bundle
	 * option is activated.
	 */
	File getBundlesDirectory();

	/**
	 * Tells if this transpiler generates JavaScript bundles for running in a
	 * Web browser.
	 */
	boolean isBundle();

	/**
	 * Gets the expected Java source code encoding.
	 */
	String getEncoding();

	/**
	 * Tells if this transpiler skips the root directories (packages annotated
	 * with @jsweet.lang.Root) so that the generated file hierarchy starts at
	 * the root directories rather than including the entire directory
	 * structure.
	 */
	boolean isNoRootDirectories();

	/**
	 * Tells if the transpiler should ignore the 'assert' statements or generate
	 * appropriate code.
	 */
	boolean isIgnoreAssertions();

	/**
	 * Generates output code even if the main class is not placed within a file
	 * of the same name.
	 */
	boolean isIgnoreJavaFileNameError();

	/**
	 * Generates d.ts files along with the js files.
	 */
	boolean isGenerateDeclarations();

	/**
	 * The directory where the transpiler should put the extracted JavaScript
	 * files from candies. Candies can bundle one or more JavaScript files that
	 * will be extracted to this directory.
	 */
	File getExtractedCandyJavascriptDir();

	/**
	 * Returns true if the JDK accesses are allowed.
	 */
	boolean isJDKAllowed();

	/**
	 * If false, do not compile TypeScript output (let an external TypeScript
	 * compiler do so). Default is true.
	 */
	boolean isGenerateJsFiles();

	/**
	 * If true, JSweet will keep track of implemented interfaces in objects at
	 * runtime, so that the instanceof operator can work properly with
	 * interfaces.
	 * 
	 * <p>
	 * If false, instanceof will always return false for an interface. Also, if
	 * false, method overloading will not work efficiently when the arguments
	 * are interfaces, leading to 'invalid overload' errors.
	 * 
	 * <p>
	 * Programmers may want to disable the instanceof operator to have lighter
	 * objects and less polluted JavaScript code. However, they must remain in a
	 * pure JavaScript use case.
	 */
	boolean isInterfaceTracking();

	/**
	 * If true, JSweet will keep track of the class names in the corresponding
	 * constructors, so that the object.getClass().getName() can work properly.
	 */
	boolean isSupportGetClass();

	/**
	 * If true, JSweet will implement a lazy initialization mechanism of static
	 * fields and initializers, to emulate the Java behavior.
	 */
	boolean isSupportSaticLazyInitialization();

	/**
	 * Generated definitions from def.* packages in d.ts files.
	 */
	boolean isGenerateDefinitions();

}