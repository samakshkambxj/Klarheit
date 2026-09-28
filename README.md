# Klarheit

Single-project fork combining ViPER4Android + JamesDSP into one driver + one app, with Magisk and AOSP ROM outputs.

## Current state: ViPER base import

* `app/` — from `likelikeslike/ViPER4Android@main`
* `drivers/viper/` — from `likelikeslike/ViPERFX_RE@dev` (+ `ViPERDSP` submodule)
* Build app: `make -f Makefile.app debug|release`
* Build driver: `make -C drivers/viper libs|zip`
* Upstream READMEs preserved in `docs/UPSTREAM-*.md`

## Roadmap

1. ✅ ViPER base import (this commit)
2. NEXT: Material 3 Expressive upgrade + Klarheit branding (app only, no DSP change)
3. JamesDSP merge: port `JamesDSPManager/Main` blocks into `drivers/klarheit/` as second stage, single UUID
4. AOSP: `Android.bp` + `device.mk` + sepolicy + priv-app permissions

See `~/Klarheit_effects.md` for the 31-effect target list.

## Attribution

Original ViPER4Android by Zhuhang / ViPER520. Reverse engineering by Martmists, Iscle, likelikeslike. JamesDSP by james34602. See `docs/ATTRIBUTION.md` and `LICENSE`.
