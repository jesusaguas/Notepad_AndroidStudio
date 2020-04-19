package es.unizar.eina.send;

import android.app.Activity;

/**
 * Define la interfaz para las clases de la implementacion.
 * La interfaz no se tiene que corresponder directamente con la interfaz de la abstraccion.
 *
 */
public interface SendImplementor {

    /**  Actualiza la actividad desde la cual se abrira la actividad de envi­o de notas */
    public void setSourceActivity(Activity source);

    /**  Recupera la actividad desde la cual se abrira la actividad de envio de notas */
    public Activity getSourceActivity();

    /** Permite lanzar la actividad encargada de gestionar el envio de notas */
    public void send(String subject, String body);

}
