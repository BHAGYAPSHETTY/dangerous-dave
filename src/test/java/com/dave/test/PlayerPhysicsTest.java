package com.dave.test;

import com.dave.model.Dave;

public class PlayerPhysicsTest {
    public static void runAll() {
        testJumpArcAndGrounding();
        testTerminalFallSpeed();
        testJetpackFlightAndFuel();
        System.out.println("  [PASS] PlayerPhysicsTest - All tests passed.");
    }

    private static void testJumpArcAndGrounding() {
        Dave dave = new Dave(50, 50);
        dave.setGrounded(false); // In mid-air

        // EXPLOIT TEST: Should NOT be able to jump while airborne
        boolean jumped = dave.jump();
        if (jumped) {
            throw new AssertionError("Air-jumping / infinite jumping exploit succeeded!");
        }

        // Now set grounded
        dave.setGrounded(true);
        jumped = dave.jump();
        if (!jumped) {
            throw new AssertionError("Dave should be able to jump when grounded!");
        }
        if (dave.getVy() >= 0) {
            throw new AssertionError("Jump velocity should be negative (upward), was: " + dave.getVy());
        }
        if (dave.isGrounded()) {
            throw new AssertionError("Dave should not be grounded immediately after jumping!");
        }
    }

    private static void testTerminalFallSpeed() {
        Dave dave = new Dave(50, 50);
        dave.setGrounded(false);

        // Simulate 100 gravity ticks
        for (int i = 0; i < 100; i++) {
            dave.updatePhysics();
        }

        if (dave.getVy() > Dave.MAX_FALL_SPEED) {
            throw new AssertionError("Fall speed exceeded terminal velocity: " + dave.getVy());
        }
    }

    private static void testJetpackFlightAndFuel() {
        Dave dave = new Dave(50, 50);
        dave.equipJetpack();

        if (!dave.hasJetpack()) {
            throw new AssertionError("Dave should have jetpack equipped!");
        }
        if (dave.getJetpackFuel() != Dave.MAX_JETPACK_FUEL) {
            throw new AssertionError("Jetpack should start with max fuel!");
        }

        dave.toggleJetpack();
        if (!dave.isJetpackActive()) {
            throw new AssertionError("Jetpack should be active after toggle!");
        }

        // Fly until fuel depletes
        for (int i = 0; i < 400; i++) {
            dave.updatePhysics();
        }

        if (dave.getJetpackFuel() != 0) {
            throw new AssertionError("Jetpack fuel should deplete to 0!");
        }
        if (dave.isJetpackActive()) {
            throw new AssertionError("Jetpack should automatically shut off when fuel is 0!");
        }
    }
}
