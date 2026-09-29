#pragma once

#include "KlarheitParams.h"
#include "effects/AnalogX.h"
#include "effects/ColorfulMusic.h"
#include "effects/Convolver.h"
#include "effects/Cure.h"
#include "effects/DiffSurround.h"
#include "effects/DynamicEQ.h"
#include "effects/DynamicSystem.h"
#include "effects/FETCompressor.h"
#include "effects/IIRFilter.h"
#include "effects/LUFSTargeting.h"
#include "effects/MultibandCompressor.h"
#include "effects/PlaybackGain.h"
#include "effects/PsychoacousticBass.h"
#include "effects/Reverberation.h"
#include "effects/SoftwareLimiter.h"
#include "effects/SpeakerCorrection.h"
#include "effects/SpectrumExtend.h"
#include "effects/StereoImager.h"
#include "effects/TubeSimulator.h"
#include "effects/VHE.h"
#include "effects/KlarheitBass.h"
#include "effects/KlarheitBassMono.h"
#include "effects/KlarheitClarity.h"
#include "effects/KlarheitDDC.h"
#include "utils/AdaptiveBuffer.h"
#include "utils/WaveBuffer.h"
#include <array>
#include <atomic>
#include <optional>
#include <vector>

class Klarheit {
public:
    Klarheit();

    void Process(std::vector<float> &buffer, uint32_t size);
    bool DispatchParamValue(int param, const klarheit::ParamValue &value);

    void RequestEffectsReset();
    void ResetAllEffects();

    void RequestBuffersReset();
    void ResetBuffers();

    void ApplyParams(const klarheit::KlarheitParams &params);
    void ApplyMasterLimiter(const klarheit::MasterLimiterParams &p);
    void ApplyPlaybackGainControl(const klarheit::PlaybackGainControlParams &p);
    void ApplyLufs(const klarheit::LufsParams &p);
    void ApplyFetCompressor(const klarheit::FetCompressorParams &p);
    void ApplyBass(const klarheit::BassParams &p);
    void ApplyBassMono(const klarheit::BassMonoParams &p);
    void ApplyPsychoacousticBass(const klarheit::PsychoacousticBassParams &p);
    void ApplySpectrumExtension(const klarheit::SpectrumExtensionParams &p);
    void ApplyEqualizer(const klarheit::EqualizerParams &p);
    void ApplyConvolver(const klarheit::ConvolverParams &p);
    void ApplyDdc(const klarheit::DdcParams &p);
    void ApplyFieldSurround(const klarheit::FieldSurroundParams &p);
    void ApplyDiffSurround(const klarheit::DiffSurroundParams &p);
    void ApplyStereoImager(const klarheit::StereoImagerParams &p);
    void ApplyHeadphoneSurround(const klarheit::HeadphoneSurroundParams &p);
    void ApplyReverb(const klarheit::ReverbParams &p);
    void ApplyDynamicSystem(const klarheit::DynamicSystemParams &p);
    void ApplyClarity(const klarheit::ClarityParams &p);
    void ApplyCure(const klarheit::CureParams &p);
    void ApplyTubeSimulator(const klarheit::TubeSimulatorParams &p);
    void ApplyAnalogX(const klarheit::AnalogXParams &p);
    void ApplySpeakerCorrection(const klarheit::SpeakerCorrectionParams &p);
    void ApplyMultibandCompressor(const klarheit::MultibandCompressorParams &p);
    void ApplyDynamicEq(const klarheit::DynamicEqParams &p);

    // Typed Convolver kernel loader.
    std::optional<uint32_t> LoadConvolverKernel(
        const float *samples, uint32_t frame_count, uint32_t channels, uint32_t kernel_id
    );

    // Typed wrapper for the kernel-unload path.
    void UnloadConvolverKernel();

    // Typed DDC coefficient loader.
    void LoadDdcCoefficients(
        const klarheit::BiquadSection *sections44100,
        const klarheit::BiquadSection *sections48000,
        uint32_t section_count
    );

    const klarheit::KlarheitParams &CurrentParams() const { return last_applied_; }

    [[nodiscard]] uint32_t GetSamplingRate() const { return sampling_rate_; }
    [[nodiscard]] uint64_t GetProcessedFrames() const { return process_frame_count_; }
    [[nodiscard]] uint32_t GetConvolverKernelID() const {
        return convolver_.GetKernelID();
    }

    void SetSamplingRate(const uint32_t rate) { sampling_rate_ = rate; }

private:
    std::atomic<bool> pending_effects_reset_{false};
    std::atomic<bool> pending_buffers_reset_{false};

    uint32_t sampling_rate_;
    uint64_t process_frame_count_;

    float frame_scale_;
    float left_pan_;
    float right_pan_;

    // Effects
    AdaptiveBuffer adaptive_buffer_;
    WaveBuffer wave_buffer_;
    Convolver convolver_;
    VHE vhe_;
    KlarheitDDC klarheit_ddc_;
    SpectrumExtend spectrum_extend_;
    StereoImager stereo_imager_;
    IIRFilter iir_filter_;
    DynamicEQ dynamic_eq_;
    ColorfulMusic colorful_music_;
    Reverberation reverberation_;
    PlaybackGain playback_gain_;
    LUFSTargeting lufs_targeting_;
    FETCompressor fet_compressor_;
    MultibandCompressor multiband_compressor_;
    DynamicSystem dynamic_system_;
    KlarheitBass klarheit_bass_;
    KlarheitBassMono klarheit_bass_mono_;
    PsychoacousticBass psychoacoustic_bass_;
    KlarheitClarity klarheit_clarity_;
    DiffSurround diff_surround_;
    Cure cure_;
    TubeSimulator tube_simulator_;
    AnalogX analog_x_;
    SpeakerCorrection speaker_correction_;
    std::array<SoftwareLimiter, 2> software_limiters_;

    klarheit::KlarheitParams last_applied_;
};
