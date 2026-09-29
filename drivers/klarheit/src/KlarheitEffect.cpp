#include "KlarheitContext.h"
#include "essential.h"
#include "log.h"
#include "klarheit/constants.h"

extern "C" {
struct KlarheitHandle {
    const effect_interface_s *interface; // Always keep as first member
    KlarheitContext *context;
};

static const effect_descriptor_t kKlarheitDescriptor = {
    .type = *EFFECT_UUID_NULL,
    .uuid = {0x90380da3, 0x8536, 0x4744, 0xa6a3, {0x57, 0x31, 0x97, 0x0e, 0x64, 0x0f}},
    .api_version = EFFECT_CONTROL_API_VERSION,
    .flags = EFFECT_FLAG_OUTPUT_DIRECT | EFFECT_FLAG_INPUT_DIRECT
             | EFFECT_FLAG_INSERT_LAST | EFFECT_FLAG_TYPE_INSERT,
    .cpu_load = 8, // In 0.1 MIPS units as estimated on an ARM9E core (ARMv5TE) with 0 WS
    .memory_usage = 1, // In KB and includes only dynamically allocated memory
    .name = KLARHEIT_NAME,
    .implementor = KLARHEIT_AUTHORS
};

static int32_t KlarheitInterfaceProcess(
    effect_handle_t self, audio_buffer_t *in_buffer, audio_buffer_t *out_buffer
) {
    const auto klarheit_handle = reinterpret_cast<KlarheitHandle *>(self);
    if (klarheit_handle == nullptr) return -EINVAL;

    return klarheit_handle->context->Process(in_buffer, out_buffer);
}

static int32_t KlarheitInterfaceCommand(
    effect_handle_t self,
    const uint32_t cmd_code,
    const uint32_t cmd_size,
    void *cmd_data,
    uint32_t *reply_size,
    void *reply_data
) {
    const auto klarheit_handle = reinterpret_cast<KlarheitHandle *>(self);
    if (klarheit_handle == nullptr) return -EINVAL;

    return klarheit_handle->context->HandleCommand(
        cmd_code, cmd_size, cmd_data, reply_size, reply_data
    );
}

static int32_t KlarheitInterfaceGetDescriptor(
    effect_handle_t self, effect_descriptor_t *descriptor
) {
    if (descriptor == nullptr) return -EINVAL;
    *descriptor = kKlarheitDescriptor;
    return 0;
}

static constexpr effect_interface_s kKlarheitInterface = {
    .process = KlarheitInterfaceProcess,
    .command = KlarheitInterfaceCommand,
    .get_descriptor = KlarheitInterfaceGetDescriptor
};

static int32_t KlarheitLibraryCreate(
    const effect_uuid_t *uuid, int32_t session_id, int32_t io_id, effect_handle_t *handle
) {
    if (uuid == nullptr || handle == nullptr) return -EINVAL;
    if (memcmp(uuid, &kKlarheitDescriptor.uuid, sizeof(effect_uuid_t)) != 0) return -ENOENT;

    auto *klarheit_handle = new KlarheitHandle();
    klarheit_handle->interface = &kKlarheitInterface;
    klarheit_handle->context = new KlarheitContext();
    *handle = reinterpret_cast<effect_handle_t>(klarheit_handle);
    KLARHEIT_LOGI(
        "KlarheitLibraryCreate: session_id=%d, io_id=%d, context=%p",
        session_id,
        io_id,
        klarheit_handle->context
    );
    return 0;
}

static int32_t KlarheitLibraryRelease(effect_handle_t handle) {
    const auto klarheit_handle = reinterpret_cast<KlarheitHandle *>(handle);
    if (klarheit_handle == nullptr) return -EINVAL;

    KLARHEIT_LOGI("KlarheitLibraryRelease: context=%p", klarheit_handle->context);
    delete klarheit_handle->context;
    return 0;
}

static int32_t KlarheitLibraryGetDescriptor(
    const effect_uuid_t *uuid, effect_descriptor_t *descriptor
) {
    if (uuid == nullptr || descriptor == nullptr) return -EINVAL;
    if (memcmp(uuid, &kKlarheitDescriptor.uuid, sizeof(effect_uuid_t)) != 0) return -ENOENT;

    *descriptor = kKlarheitDescriptor;
    return 0;
}

__attribute__((visibility("default"))) audio_effect_library_t
    AUDIO_EFFECT_LIBRARY_INFO_SYM = {
        .tag = AUDIO_EFFECT_LIBRARY_TAG,
        .version = EFFECT_LIBRARY_API_VERSION,
        .name = KLARHEIT_NAME,
        .implementor = KLARHEIT_AUTHORS,
        .create_effect = KlarheitLibraryCreate,
        .release_effect = KlarheitLibraryRelease,
        .get_descriptor = KlarheitLibraryGetDescriptor,
};
} // extern "C"
