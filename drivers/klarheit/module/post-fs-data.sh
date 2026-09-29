LIBPATCH=$(cat $MODPATH/libpatch.txt)
CFGS="$(find /odm /system /vendor -type f -name "*audio_effects*.conf" -o -name "*audio_effects*.xml")"
for FILE in ${CFGS}; do
  case $FILE in
  *.conf)
    # Legacy pre-rebrand entries (upgrades from the ViPER-named module)
    sed -i "/v4a_standard_re {/,/}/d" $FILE
    sed -i "/v4a_re {/,/}/d" $FILE
    sed -i "/klarheit_standard {/,/}/d" $FILE
    sed -i "/klarheit {/,/}/d" $FILE
    sed -i "s/^effects {/effects {\n  klarheit_standard {\n    library klarheit\n    uuid 90380da3-8536-4744-a6a3-5731970e640f\n  }/g" $FILE
    sed -i "s/^libraries {/libraries {\n  klarheit {\n    path $LIBPATCH\/lib\/soundfx\/libklarheit.so\n  }/g" $FILE
    ;;
  *.xml)
    # Legacy pre-rebrand entries (upgrades from the ViPER-named module)
    sed -i "/v4a_standard_re/d" $FILE
    sed -i "/v4a_re/d" $FILE
    sed -i "/klarheit_standard/d" $FILE
    sed -i "/klarheit/d" $FILE
    sed -i "/<libraries>/ a\        <library name=\"klarheit\" path=\"libklarheit.so\"\/>" $FILE
    sed -i "/<effects>/ a\        <effect name=\"klarheit_standard\" library=\"klarheit\" uuid=\"90380da3-8536-4744-a6a3-5731970e640f\"\/>" $FILE
    ;;
  esac
done

if [ -d "/odm/etc/" ]; then
  echo "Binding audio_effects.xml to odm partition..."
  mount -o bind /data/adb/modules/klarheit/odm/etc/audio_effects.xml /odm/etc/audio_effects.xml
fi
