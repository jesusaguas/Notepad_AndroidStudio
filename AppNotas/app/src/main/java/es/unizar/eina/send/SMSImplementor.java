package es.unizar.eina.send;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/** Concrete implementor utilizando aplicacion por defecto de Android para gestionar mail. No funciona en el emulador si no se ha configurado previamente el mail */
public class SMSImplementor implements SendImplementor{

    /** actividad desde la cual se abrira la actividad de gestión de correo */
    private Activity sourceActivity;

    /** Constructor
     * @param source actividad desde la cual se abrira la actividad de gestion de correo
     */
    public SMSImplementor(Activity source){
        setSourceActivity(source);
    }

    /**  Actualiza la actividad desde la cual se abrira la actividad de gestion de correo */
    public void setSourceActivity(Activity source) {
        sourceActivity = source;
    }

    /**  Recupera la actividad desde la cual se abrira la actividad de gestion de correo */
    public Activity getSourceActivity(){
        return sourceActivity;
    }

    /**
     * Implementacion del metodo send utilizando la aplicacion de gestion de correo de Android
     * Solo se copia el asunto y el cuerpo
     * @param subject asunto
     * @param body cuerpo del mensaje
     */
    public void send (String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        intent.putExtra("sms_body", subject+": "+body);
        //intent.setType("vnd.android-dir/mms-sms");
        getSourceActivity().startActivity(intent);
    }

}
