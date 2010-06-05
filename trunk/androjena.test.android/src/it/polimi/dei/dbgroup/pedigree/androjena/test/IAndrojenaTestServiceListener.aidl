/*
 * Copyright 2010 Lorenzo Carrara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polimi.dei.dbgroup.pedigree.androjena.test;

import it.polimi.dei.dbgroup.pedigree.androjena.test.ParcelableException;

oneway interface IAndrojenaTestServiceListener
{
	void testsStarted(int suiteCount, long maxMemory);
	void suiteStarted(String suiteName, int testCount);
	void suiteEnded(String suiteName);
	void testStarted(String testName, String testClass, long freeMemory, long totalMemory);
	void testError(String testName, long freeMemory, long totalMemory, in ParcelableException ex);
	void testFailed(String testName, long freeMemory, long totalMemory, in ParcelableException ex);
	void testEnded(String testName, long freeMemory, long totalMemory);
	void testsEnded(boolean success, in ParcelableException ex);
}