package mdl.sinlov.android.websocket.app.utils;

import android.content.Context;

/**
 * get Resource by name
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by "sinlov" on 16/8/26.
 */
public class ResourceUtil {

    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout",
                paramContext.getPackageName());
    }

    public static int getStringId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "string",
                paramContext.getPackageName());
    }
    public static String getStrByRes(Context context, String key){
        return context.getString(context.getResources().getIdentifier(key, "string",
                context.getPackageName()));
    }
    public static String getStrByRes(Context context, String key, Object...formatArgs){
        return context.getString(context.getResources().getIdentifier(key, "string",
                context.getPackageName()), formatArgs);
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }

    public static int getMipmapId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "mipmap", paramContext.getPackageName());
    }


    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "style",
                paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "id",
                paramContext.getPackageName());
    }

    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "color",
                paramContext.getPackageName());
    }

    public static int getDimenId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "dimen",
                paramContext.getPackageName());
    }

    public static int getAnimId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "anim",
                paramContext.getPackageName());
    }

}
