package com.dave.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * High-performance procedural retro 8-bit sound synthesizer.
 * Generates square, triangle, and noise audio waveforms in real time using the standard Java Sound API.
 * Operates gracefully in headless/silent environments with zero crash risk.
 */
public class RetroAudioEngine {
    private static final int SAMPLE_RATE = 22050;
    private final ExecutorService audioExecutor;
    private boolean enabled;
    private boolean muted;

    public RetroAudioEngine() {
        this.audioExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Dave-Audio-Synth");
            t.setDaemon(true);
            return t;
        });
        this.enabled = true;
        this.muted = false;
    }

    public void playSound(SoundEffect sfx) {
        if (!enabled || muted || sfx == null) return;

        audioExecutor.submit(() -> {
            try {
                byte[] audioData = synthesizeSound(sfx);
                if (audioData != null && audioData.length > 0) {
                    playPcm(audioData);
                }
            } catch (Exception ignored) {
                // Audio unavailable or audio device busy - silent fallback
            }
        });
    }

    private byte[] synthesizeSound(SoundEffect sfx) {
        switch (sfx) {
            case JUMP:
                // Upward pitch bend square wave (160Hz -> 480Hz, 120ms)
                return generatePitchSweep(160, 480, 0.12f, true);

            case PICKUP:
                // High dual-tone chime (587Hz -> 880Hz, 100ms)
                return generateDualTone(587, 880, 0.10f);

            case TROPHY:
                // Grand triumphant 4-note arpeggio (C5 -> E5 -> G5 -> C6)
                return generateArpeggio(new int[]{523, 659, 784, 1046}, 0.08f);

            case SHOOT:
                // Crisp pop: High frequency square drop + noise burst (70ms)
                return generateNoiseBurst(0.07f, 600, 150);

            case EXPLODE:
                // Deep low-frequency noise rumble with decay (300ms)
                return generateExplosionRumble(0.30f);

            case JETPACK:
                // Low rhythmic engine hum (60ms)
                return generateSquareWave(110, 0.06f, 0.5f);

            case DOOR:
                // Level clear chime fanfare
                return generateArpeggio(new int[]{440, 554, 659, 880, 1108}, 0.10f);

            default:
                return null;
        }
    }

    private byte[] generateSquareWave(float freq, float durationSec, float volume) {
        int numSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[numSamples];
        double period = SAMPLE_RATE / freq;
        for (int i = 0; i < numSamples; i++) {
            double pos = i % period;
            float sample = (pos < period / 2.0) ? 1.0f : -1.0f;
            // Linear envelope decay
            float envelope = 1.0f - ((float) i / numSamples);
            buffer[i] = (byte) (sample * volume * envelope * 127);
        }
        return buffer;
    }

    private byte[] generatePitchSweep(float startFreq, float endFreq, float durationSec, boolean square) {
        int numSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[numSamples];
        double phase = 0;
        for (int i = 0; i < numSamples; i++) {
            float progress = (float) i / numSamples;
            float currentFreq = startFreq + (endFreq - startFreq) * progress;
            phase += 2 * Math.PI * currentFreq / SAMPLE_RATE;
            float sample;
            if (square) {
                sample = Math.sin(phase) >= 0 ? 1.0f : -1.0f;
            } else {
                sample = (float) Math.sin(phase);
            }
            float envelope = 1.0f - progress * 0.4f;
            buffer[i] = (byte) (sample * 0.7f * envelope * 127);
        }
        return buffer;
    }

    private byte[] generateDualTone(float f1, float f2, float durationSec) {
        int half = (int) (SAMPLE_RATE * durationSec / 2);
        byte[] b1 = generateSquareWave(f1, durationSec / 2, 0.7f);
        byte[] b2 = generateSquareWave(f2, durationSec / 2, 0.8f);
        byte[] combined = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, combined, 0, b1.length);
        System.arraycopy(b2, 0, combined, b1.length, b2.length);
        return combined;
    }

    private byte[] generateArpeggio(int[] frequencies, float noteDurationSec) {
        int noteSamples = (int) (SAMPLE_RATE * noteDurationSec);
        byte[] total = new byte[noteSamples * frequencies.length];
        for (int note = 0; note < frequencies.length; note++) {
            byte[] n = generateSquareWave(frequencies[note], noteDurationSec, 0.75f);
            System.arraycopy(n, 0, total, note * noteSamples, Math.min(n.length, noteSamples));
        }
        return total;
    }

    private byte[] generateNoiseBurst(float durationSec, float startFreq, float endFreq) {
        int numSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[numSamples];
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < numSamples; i++) {
            float progress = (float) i / numSamples;
            float noise = (rnd.nextFloat() * 2f - 1f) * 0.5f;
            float tone = (float) Math.sin(2 * Math.PI * (startFreq - (startFreq - endFreq) * progress) * i / SAMPLE_RATE) * 0.5f;
            float sample = noise + tone;
            float envelope = (1.0f - progress);
            buffer[i] = (byte) (sample * envelope * 127);
        }
        return buffer;
    }

    private byte[] generateExplosionRumble(float durationSec) {
        int numSamples = (int) (SAMPLE_RATE * durationSec);
        byte[] buffer = new byte[numSamples];
        java.util.Random rnd = new java.util.Random();
        float lastVal = 0;
        for (int i = 0; i < numSamples; i++) {
            float progress = (float) i / numSamples;
            // Simple low-pass filter on white noise
            float white = rnd.nextFloat() * 2f - 1f;
            lastVal = lastVal * 0.85f + white * 0.15f;
            float envelope = (float) Math.pow(1.0f - progress, 1.5);
            buffer[i] = (byte) (lastVal * envelope * 127);
        }
        return buffer;
    }

    private void playPcm(byte[] audioData) {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format, audioData.length);
            line.start();
            line.write(audioData, 0, audioData.length);
            line.drain();
        } catch (LineUnavailableException | IllegalArgumentException e) {
            // Audio hardware not present or supported - silent
            this.enabled = false;
        }
    }

    public boolean isMuted() { return muted; }
    public void toggleMute() { this.muted = !this.muted; }
    public void shutdown() {
        audioExecutor.shutdownNow();
    }
}
