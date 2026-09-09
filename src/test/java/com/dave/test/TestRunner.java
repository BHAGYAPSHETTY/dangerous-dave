package com.dave.test;

/**
 * Headless standalone test suite runner for Dangerous Dave.
 * Executes all unit and anti-exploit regression tests without requiring third-party libraries.
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DANGEROUS DAVE - AUTOMATED TEST SUITE RUNNER   ");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        try {
            System.out.println("\n[1/4] Running Collision & Boundary Tests...");
            CollisionTest.runAll();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] CollisionTest failed: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            System.out.println("\n[2/4] Running Player Physics & Jump Arc Tests...");
            PlayerPhysicsTest.runAll();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] PlayerPhysicsTest failed: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            System.out.println("\n[3/4] Running Anti-Exploit & Loophole Tests...");
            ExploitTest.runAll();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] ExploitTest failed: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            System.out.println("\n[4/5] Running State Machine Safety Tests...");
            StateMachineTest.runAll();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] StateMachineTest failed: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            System.out.println("\n[5/5] Running Full 10-Level Simulation & Engine Integration Tests...");
            GameplaySimulationTest.main(new String[0]);
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] GameplaySimulationTest failed: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        System.out.println("\n==================================================");
        System.out.println(String.format("TEST RESULTS: %d Passed, %d Failed", passed, failed));
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        } else {
            System.out.println("ALL REGRESSION & ANTI-EXPLOIT TESTS PASSED SUCCESSFULLY!");
            System.exit(0);
        }
    }
}
