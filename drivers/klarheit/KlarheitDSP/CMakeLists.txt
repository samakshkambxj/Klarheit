cmake_minimum_required(VERSION 3.16)
project(KlarheitDSP LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(KLARHEITDSP_SOURCES
    # FFT
    klarheit/utils/pffft.c

    # Engine
    klarheit/Klarheit.cpp

    # Effects
    klarheit/effects/AnalogX.cpp
    klarheit/effects/ColorfulMusic.cpp
    klarheit/effects/Convolver.cpp
    klarheit/effects/Cure.cpp
    klarheit/effects/DiffSurround.cpp
    klarheit/effects/DynamicEQ.cpp
    klarheit/effects/DynamicSystem.cpp
    klarheit/effects/FETCompressor.cpp
    klarheit/effects/IIRFilter.cpp
    klarheit/effects/LUFSTargeting.cpp
    klarheit/effects/MultibandCompressor.cpp
    klarheit/effects/PlaybackGain.cpp
    klarheit/effects/PsychoacousticBass.cpp
    klarheit/effects/Reverberation.cpp
    klarheit/effects/SoftwareLimiter.cpp
    klarheit/effects/SpeakerCorrection.cpp
    klarheit/effects/SpectrumExtend.cpp
    klarheit/effects/StereoImager.cpp
    klarheit/effects/TubeSimulator.cpp
    klarheit/effects/VHE.cpp
    klarheit/effects/KlarheitBass.cpp
    klarheit/effects/KlarheitBassMono.cpp
    klarheit/effects/KlarheitClarity.cpp
    klarheit/effects/KlarheitDDC.cpp

    # Utils
    klarheit/utils/AdaptiveBuffer.cpp
    klarheit/utils/Biquad.cpp
    klarheit/utils/CAllPassFilter.cpp
    klarheit/utils/CCombFilter.cpp
    klarheit/utils/CRevModel.cpp
    klarheit/utils/Crossfeed.cpp
    klarheit/utils/DepthSurround.cpp
    klarheit/utils/DynamicBass.cpp
    klarheit/utils/FIR.cpp
    klarheit/utils/Harmonic.cpp
    klarheit/utils/HiFi.cpp
    klarheit/utils/HighShelf.cpp
    klarheit/utils/IIR_1st.cpp
    klarheit/utils/IIR_NOrder_BW_BP.cpp
    klarheit/utils/IIR_NOrder_BW_LH.cpp
    klarheit/utils/MinPhaseIIRCoeffs.cpp
    klarheit/utils/MultiBiquad.cpp
    klarheit/utils/NoiseSharpening.cpp
    klarheit/utils/PassFilter.cpp
    klarheit/utils/PConvSingle.cpp
    klarheit/utils/PolesFilter.cpp
    klarheit/utils/Polyphase.cpp
    klarheit/utils/Stereo3DSurround.cpp
    klarheit/utils/Subwoofer.cpp
    klarheit/utils/TimeConstDelay.cpp
    klarheit/utils/WaveBuffer.cpp
)

add_library(KlarheitDSP STATIC ${KLARHEITDSP_SOURCES})

target_include_directories(KlarheitDSP PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}
)

target_compile_options(KlarheitDSP PRIVATE -O3 -flto)

if (ANDROID)
    target_link_libraries(KlarheitDSP log)
endif ()
