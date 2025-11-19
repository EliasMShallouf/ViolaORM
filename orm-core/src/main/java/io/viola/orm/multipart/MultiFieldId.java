package io.viola.orm.multipart;

import io.viola.orm.helpers.ClassHelper;

import java.lang.reflect.Field;
import java.util.Map;

public interface MultiFieldId<Obj> {
    Map<String, ?> values();

    static <Obj, MFI extends MultiFieldId<Obj>> MFI from(Class<MFI> clazz, Obj o) {
        return create(ClassHelper.createObject(clazz), o);
    }

    static <Obj, MFI extends MultiFieldId<Obj>> MFI create(MFI obj, Obj o) {
        for(Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);

            try {
                inner: for(Field fo : o.getClass().getDeclaredFields()) {
                    if(f.getName().equals(fo.getName())) {
                        fo.setAccessible(true);
                        f.set(obj, fo.get(o));
                        break inner;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return obj;
    }
}
