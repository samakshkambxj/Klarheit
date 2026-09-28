package com.klarheit.audio.klarheit

import com.klarheit.audio.effect.AnalogXState
import com.klarheit.audio.effect.BassMonoState
import com.klarheit.audio.effect.BassState
import com.klarheit.audio.effect.ClarityState
import com.klarheit.audio.effect.ConvolverState
import com.klarheit.audio.effect.CureState
import com.klarheit.audio.effect.DdcState
import com.klarheit.audio.effect.DiffSurroundState
import com.klarheit.audio.effect.DynamicEqState
import com.klarheit.audio.effect.DynamicSystemState
import com.klarheit.audio.effect.EffectState
import com.klarheit.audio.effect.EqState
import com.klarheit.audio.effect.FetCompressorState
import com.klarheit.audio.effect.FieldSurroundState
import com.klarheit.audio.effect.HeadphoneSurroundState
import com.klarheit.audio.effect.LufsState
import com.klarheit.audio.effect.MultibandCompressorState
import com.klarheit.audio.effect.OutputState
import com.klarheit.audio.effect.PlaybackGainControlState
import com.klarheit.audio.effect.PsychoacousticBassState
import com.klarheit.audio.effect.ReverbState
import com.klarheit.audio.effect.SpeakerCorrectionState
import com.klarheit.audio.effect.SpectrumExtensionState
import com.klarheit.audio.effect.StereoImagerState
import com.klarheit.audio.effect.TubeSimulatorState
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Serialize an EffectState to the byte layout of klarheit::KlarheitParams as defined in KlarheitParamsLayout.kt.
 */
object KlarheitParamsSerializer {
    fun toByteArray(state: EffectState): ByteArray {
        val buf =
            ByteBuffer.allocate(KlarheitParamsLayout.SIZE).order(ByteOrder.LITTLE_ENDIAN)
        write(buf, 0, state)
        return buf.array()
    }

    /**
     * Serialize the entire KlarheitParams struct into [buf] starting at [offset].
     */
    fun write(
        buf: ByteBuffer,
        offset: Int,
        state: EffectState,
    ) {
        require(buf.order() == ByteOrder.LITTLE_ENDIAN) {
            "ByteBuffer must be little-endian to match C++ struct layout"
        }
        for (i in 0 until KlarheitParamsLayout.SIZE) {
            buf.put(offset + i, 0)
        }

        writeMasterLimiter(
            buf,
            offset + KlarheitParamsLayout.MASTER_LIMITER,
            state.out,
        )
        writePlaybackGainControl(
            buf,
            offset + KlarheitParamsLayout.PLAYBACK_GAIN_CONTROL,
            state.playbackGainControl,
        )
        writeLufs(buf, offset + KlarheitParamsLayout.LUFS, state.lufs)
        writeFetCompressor(
            buf,
            offset + KlarheitParamsLayout.FET_COMPRESSOR,
            state.fetCompressor,
        )
        writeBass(buf, offset + KlarheitParamsLayout.BASS, state.bass)
        writeBassMono(buf, offset + KlarheitParamsLayout.BASS_MONO, state.bassMono)
        writePsychoacousticBass(
            buf,
            offset + KlarheitParamsLayout.PSYCHOACOUSTIC_BASS,
            state.psychoacousticBass,
        )
        writeSpectrumExtension(
            buf,
            offset + KlarheitParamsLayout.SPECTRUM_EXTENSION,
            state.spectrumExtension,
        )
        writeEqualizer(buf, offset + KlarheitParamsLayout.EQUALIZER, state.eq)
        writeConvolver(buf, offset + KlarheitParamsLayout.CONVOLVER, state.convolver)
        writeDdc(buf, offset + KlarheitParamsLayout.DDC, state.ddc)
        writeFieldSurround(
            buf,
            offset + KlarheitParamsLayout.FIELD_SURROUND,
            state.fieldSurround,
        )
        writeDiffSurround(
            buf,
            offset + KlarheitParamsLayout.DIFF_SURROUND,
            state.diffSurround,
        )
        writeStereoImager(
            buf,
            offset + KlarheitParamsLayout.STEREO_IMAGER,
            state.stereoImager,
        )
        writeHeadphoneSurround(
            buf,
            offset + KlarheitParamsLayout.HEADPHONE_SURROUND,
            state.headphoneSurround,
        )
        writeReverb(buf, offset + KlarheitParamsLayout.REVERB, state.reverb)
        writeDynamicSystem(
            buf,
            offset + KlarheitParamsLayout.DYNAMIC_SYSTEM,
            state.dynamicSystem,
        )
        writeClarity(buf, offset + KlarheitParamsLayout.CLARITY, state.clarity)
        writeCure(buf, offset + KlarheitParamsLayout.CURE, state.cure)
        writeTubeSimulator(
            buf,
            offset + KlarheitParamsLayout.TUBE_SIMULATOR,
            state.tubeSimulator,
        )
        writeAnalogX(buf, offset + KlarheitParamsLayout.ANALOG_X, state.analogX)
        writeSpeakerCorrection(
            buf,
            offset + KlarheitParamsLayout.SPEAKER_CORRECTION,
            state.speakerCorrection,
        )
        writeMultibandCompressor(
            buf,
            offset + KlarheitParamsLayout.MULTIBAND_COMPRESSOR,
            state.multibandCompressor,
        )
        writeDynamicEq(
            buf,
            offset + KlarheitParamsLayout.DYNAMIC_EQ,
            state.dynamicEq,
        )
    }

    private fun ByteBuffer.putBool(
        off: Int,
        value: Boolean,
    ) {
        this.put(off, if (value) 1 else 0)
    }

    private fun writeMasterLimiter(
        buf: ByteBuffer,
        base: Int,
        s: OutputState,
    ) {
        val l = KlarheitParamsLayout.MasterLimiter
        buf.putFloat(base + l.THRESHOLD, s.limiter)
        buf.putFloat(base + l.OUTPUT_VOLUME, s.volume)
        buf.putFloat(base + l.CHANNEL_PAN, s.channelPan)
    }

    private fun writePlaybackGainControl(
        buf: ByteBuffer,
        base: Int,
        s: PlaybackGainControlState,
    ) {
        val l = KlarheitParamsLayout.PlaybackGainControl
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.STRENGTH, s.strength)
        buf.putFloat(base + l.MAX_GAIN, s.maxGain)
        buf.putFloat(base + l.OUTPUT_THRESHOLD, s.outputThreshold)
    }

    private fun writeLufs(
        buf: ByteBuffer,
        base: Int,
        s: LufsState,
    ) {
        val l = KlarheitParamsLayout.Lufs
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.TARGET, s.target)
        buf.putFloat(base + l.MAX_GAIN, s.maxGain)
        buf.putInt(base + l.SPEED, s.speed)
    }

    private fun writeFetCompressor(
        buf: ByteBuffer,
        base: Int,
        s: FetCompressorState,
    ) {
        val l = KlarheitParamsLayout.FetCompressor
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.THRESHOLD, s.threshold)
        buf.putFloat(base + l.RATIO, s.ratio)
        buf.putFloat(base + l.KNEE, s.knee)
        buf.putBool(base + l.KNEE_AUTO, s.kneeAuto)
        buf.putFloat(base + l.GAIN, s.gain)
        buf.putBool(base + l.GAIN_AUTO, s.gainAuto)
        buf.putFloat(base + l.ATTACK, s.attack)
        buf.putBool(base + l.ATTACK_AUTO, s.attackAuto)
        buf.putFloat(base + l.RELEASE, s.release)
        buf.putBool(base + l.RELEASE_AUTO, s.releaseAuto)
        buf.putFloat(base + l.KNEE_MULTI, s.kneeMulti)
        buf.putFloat(base + l.MAX_ATTACK, s.maxAttack)
        buf.putFloat(base + l.MAX_RELEASE, s.maxRelease)
        buf.putFloat(base + l.CREST, s.crest)
        buf.putFloat(base + l.ADAPT, s.adapt)
        buf.putBool(base + l.NO_CLIP, s.noClip)
    }

    private fun writeBass(
        buf: ByteBuffer,
        base: Int,
        s: BassState,
    ) {
        val l = KlarheitParamsLayout.Bass
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.MODE, s.mode)
        buf.putInt(base + l.FREQUENCY, s.frequency)
        buf.putFloat(base + l.GAIN, s.gain)
        buf.putBool(base + l.ANTI_POP, s.antiPop)
    }

    private fun writeBassMono(
        buf: ByteBuffer,
        base: Int,
        s: BassMonoState,
    ) {
        val l = KlarheitParamsLayout.BassMono
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.MODE, s.mode)
        buf.putInt(base + l.FREQUENCY, s.frequency)
        buf.putFloat(base + l.GAIN, s.gain)
        buf.putBool(base + l.ANTI_POP, s.antiPop)
    }

    private fun writePsychoacousticBass(
        buf: ByteBuffer,
        base: Int,
        s: PsychoacousticBassState,
    ) {
        val l = KlarheitParamsLayout.PsychoacousticBass
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.CUTOFF, s.cutoff)
        buf.putFloat(base + l.INTENSITY, s.intensity)
        buf.putInt(base + l.HARMONIC_ORDER, s.harmonicOrder)
        buf.putFloat(base + l.ORIGINAL_LEVEL, s.originalLevel)
    }

    private fun writeSpectrumExtension(
        buf: ByteBuffer,
        base: Int,
        s: SpectrumExtensionState,
    ) {
        val l = KlarheitParamsLayout.SpectrumExtension
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.STRENGTH, s.strength)
        buf.putFloat(base + l.EXCITER, s.exciter)
    }

    private fun writeEqualizer(
        buf: ByteBuffer,
        base: Int,
        s: EqState,
    ) {
        val l = KlarheitParamsLayout.Equalizer
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.BAND_COUNT, s.bandCount)
        val maxBands = l.BAND_LEVELS_LEN
        s.bands.forEachIndexed { index, bandDb ->
            if (index >= maxBands) return@forEachIndexed
            buf.putFloat(base + l.BAND_LEVELS + index * 4, bandDb.toFloat())
        }
    }

    private fun writeConvolver(
        buf: ByteBuffer,
        base: Int,
        s: ConvolverState,
    ) {
        val l = KlarheitParamsLayout.Convolver
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.CROSS_CHANNEL, s.crossChannel)
    }

    private fun writeDdc(
        buf: ByteBuffer,
        base: Int,
        s: DdcState,
    ) {
        val l = KlarheitParamsLayout.Ddc
        buf.putBool(base + l.ENABLE, s.enable)
    }

    private fun writeFieldSurround(
        buf: ByteBuffer,
        base: Int,
        s: FieldSurroundState,
    ) {
        val l = KlarheitParamsLayout.FieldSurround
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.WIDENING, s.widening)
        buf.putFloat(base + l.MID_IMAGE, s.midImage)
        buf.putShort(base + l.DEPTH, s.depth.toShort())
    }

    private fun writeDiffSurround(
        buf: ByteBuffer,
        base: Int,
        s: DiffSurroundState,
    ) {
        val l = KlarheitParamsLayout.DiffSurround
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.DELAY, s.delay)
        buf.putBool(base + l.REVERSE, s.reverse)
        buf.putFloat(base + l.WET_DRY_MIX, s.wetDryMix)
        buf.putFloat(base + l.LP_CUTOFF, s.lpCutoff.toFloat())
    }

    private fun writeStereoImager(
        buf: ByteBuffer,
        base: Int,
        s: StereoImagerState,
    ) {
        val l = KlarheitParamsLayout.StereoImager
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.LOW_WIDTH, s.lowWidth)
        buf.putFloat(base + l.MID_WIDTH, s.midWidth)
        buf.putFloat(base + l.HIGH_WIDTH, s.highWidth)
        buf.putFloat(base + l.LOW_CROSSOVER, s.lowCrossover.toFloat())
        buf.putFloat(base + l.HIGH_CROSSOVER, s.highCrossover.toFloat())
    }

    private fun writeHeadphoneSurround(
        buf: ByteBuffer,
        base: Int,
        s: HeadphoneSurroundState,
    ) {
        val l = KlarheitParamsLayout.HeadphoneSurround
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.QUALITY, s.quality)
    }

    private fun writeReverb(
        buf: ByteBuffer,
        base: Int,
        s: ReverbState,
    ) {
        val l = KlarheitParamsLayout.Reverb
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putFloat(base + l.ROOM_SIZE, s.roomSize)
        buf.putFloat(base + l.WIDTH, s.width)
        buf.putFloat(base + l.DAMP, s.damp)
        buf.putFloat(base + l.WET, s.wet)
        buf.putFloat(base + l.DRY, s.dry)
    }

    private fun writeDynamicSystem(
        buf: ByteBuffer,
        base: Int,
        s: DynamicSystemState,
    ) {
        val l = KlarheitParamsLayout.DynamicSystem
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.X_COEFF_LOW, s.xLow)
        buf.putInt(base + l.X_COEFF_HIGH, s.xHigh)
        buf.putInt(base + l.Y_COEFF_LOW, s.yLow)
        buf.putInt(base + l.Y_COEFF_HIGH, s.yHigh)
        buf.putFloat(base + l.SIDE_GAIN_LOW, s.sideGainLow)
        buf.putFloat(base + l.SIDE_GAIN_HIGH, s.sideGainHigh)
        buf.putFloat(base + l.STRENGTH, s.strength)
    }

    private fun writeClarity(
        buf: ByteBuffer,
        base: Int,
        s: ClarityState,
    ) {
        val l = KlarheitParamsLayout.Clarity
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.MODE, s.mode)
        buf.putFloat(base + l.GAIN, s.gain)
    }

    private fun writeCure(
        buf: ByteBuffer,
        base: Int,
        s: CureState,
    ) {
        val l = KlarheitParamsLayout.Cure
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.CROSSFEED_PRESET, s.crossfeedPreset)
    }

    private fun writeTubeSimulator(
        buf: ByteBuffer,
        base: Int,
        s: TubeSimulatorState,
    ) {
        val l = KlarheitParamsLayout.TubeSimulator
        buf.putBool(base + l.ENABLE, s.enable)
    }

    private fun writeAnalogX(
        buf: ByteBuffer,
        base: Int,
        s: AnalogXState,
    ) {
        val l = KlarheitParamsLayout.AnalogX
        buf.putBool(base + l.ENABLE, s.enable)
        buf.putInt(base + l.MODE, s.mode)
    }

    private fun writeSpeakerCorrection(
        buf: ByteBuffer,
        base: Int,
        s: SpeakerCorrectionState,
    ) {
        val l = KlarheitParamsLayout.SpeakerCorrection
        buf.putBool(base + l.ENABLE, s.enable)
    }

    private fun writeMultibandCompressor(
        buf: ByteBuffer,
        base: Int,
        s: MultibandCompressorState,
    ) {
        val l = KlarheitParamsLayout.MultibandCompressor
        val lb = KlarheitParamsLayout.MultibandCompressorBand
        buf.putBool(base + l.ENABLE, s.enable)

        val crossovers = s.crossovers
        val bandEnables = s.bandEnables
        val thresholds = s.thresholds
        val ratios = s.ratios
        val knees = s.knees
        val kneeAutos = s.kneeAutos
        val gains = s.gains
        val gainAutos = s.gainAutos
        val attacks = s.attacks
        val attackAutos = s.attackAutos
        val releases = s.releases
        val releaseAutos = s.releaseAutos
        val kneeMultis = s.kneeMultis
        val maxAttacks = s.maxAttacks
        val maxReleases = s.maxReleases
        val crests = s.crests
        val adapts = s.adapts
        val noClips = s.noClips

        val bandCount = minOf(bandEnables.size, l.BANDS_LEN)
        buf.putInt(base + l.BAND_COUNT, bandCount)

        crossovers.forEachIndexed { i, v ->
            if (i >= l.CROSSOVER_FREQUENCIES_LEN) return@forEachIndexed
            buf.putFloat(base + l.CROSSOVER_FREQUENCIES + i * 4, v.toFloat())
        }

        for (i in 0 until bandCount) {
            val bandBase = base + l.BANDS + i * lb.SIZE
            buf.putBool(bandBase + lb.ENABLE, bandEnables.getOrFalse(i))
            buf.putFloat(bandBase + lb.THRESHOLD, thresholds.getOrZero(i))
            buf.putFloat(bandBase + lb.RATIO, ratios.getOrZero(i))
            buf.putFloat(bandBase + lb.KNEE, knees.getOrZero(i))
            buf.putBool(bandBase + lb.KNEE_AUTO, kneeAutos.getOrFalse(i))
            buf.putFloat(bandBase + lb.GAIN, gains.getOrZero(i))
            buf.putBool(bandBase + lb.GAIN_AUTO, gainAutos.getOrFalse(i))
            buf.putFloat(bandBase + lb.ATTACK, attacks.getOrZero(i))
            buf.putBool(bandBase + lb.ATTACK_AUTO, attackAutos.getOrFalse(i))
            buf.putFloat(bandBase + lb.RELEASE, releases.getOrZero(i))
            buf.putBool(bandBase + lb.RELEASE_AUTO, releaseAutos.getOrFalse(i))
            buf.putFloat(bandBase + lb.KNEE_MULTI, kneeMultis.getOrZero(i))
            buf.putFloat(bandBase + lb.MAX_ATTACK, maxAttacks.getOrZero(i))
            buf.putFloat(bandBase + lb.MAX_RELEASE, maxReleases.getOrZero(i))
            buf.putFloat(bandBase + lb.CREST, crests.getOrZero(i))
            buf.putFloat(bandBase + lb.ADAPT, adapts.getOrZero(i))
            buf.putBool(bandBase + lb.NO_CLIP, noClips.getOrFalse(i))
        }
    }

    private fun writeDynamicEq(
        buf: ByteBuffer,
        base: Int,
        s: DynamicEqState,
    ) {
        val l = KlarheitParamsLayout.DynamicEq
        val lb = KlarheitParamsLayout.DynamicEqBand
        buf.putBool(base + l.ENABLE, s.enable)

        val freqs = s.freqs
        val qs = s.qs
        val gains = s.gains
        val thresholds = s.thresholds
        val attacks = s.attacks
        val releases = s.releases
        val filterTypes = s.filterTypes

        val bandCount = minOf(s.bandCount, l.BANDS_LEN)
        buf.putInt(base + l.BAND_COUNT, bandCount)

        for (i in 0 until bandCount) {
            val bandBase = base + l.BANDS + i * lb.SIZE
            buf.putFloat(bandBase + lb.FREQUENCY, freqs.getOrZero(i).toFloat())
            buf.putFloat(bandBase + lb.Q, qs.getOrZero(i))
            buf.putFloat(bandBase + lb.GAIN, gains.getOrZero(i))
            buf.putFloat(bandBase + lb.THRESHOLD, thresholds.getOrZero(i))
            buf.putFloat(bandBase + lb.ATTACK, attacks.getOrZero(i))
            buf.putFloat(bandBase + lb.RELEASE, releases.getOrZero(i))
            buf.putInt(bandBase + lb.FILTER_TYPE, filterTypes.getOrZero(i))
        }
    }

    private fun List<Int>.getOrZero(i: Int): Int = if (i in indices) this[i] else 0

    private fun List<Float>.getOrZero(i: Int): Float = if (i in indices) this[i] else 0.0f

    private fun List<Boolean>.getOrFalse(i: Int): Boolean = if (i in indices) this[i] else false
}
