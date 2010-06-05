package it.polimi.dei.dbgroup.pedigree.androjena.test;

import it.polimi.dei.dbgroup.pedigree.androjena.test.IAndrojenaTestServiceListener;

interface IAndrojenaTestService
{
	boolean startTests();
	boolean stopTests();
	boolean isTesting();
    void registerListener(IAndrojenaTestServiceListener listener);
    void unregisterListener(IAndrojenaTestServiceListener listener);
}