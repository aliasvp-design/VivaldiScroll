# Vivaldi Scroll 0.1 — prototipo experimental

Proyecto Android Java para abrir en Android Studio (JDK 17, Android Gradle Plugin 8.7.3).

## Instalación
1. Abrir esta carpeta como proyecto en Android Studio.
2. Sincronizar Gradle y compilar APK de depuración (Build > Build APK(s)).
3. Instalar APK en el móvil y activar Vivaldi Scroll en Ajustes > Accesibilidad.
4. Abrir Vivaldi: tocar flecha para subir; pulsación larga para bajar.

## Privacidad
- No declara permiso INTERNET ni permisos de captura.
- `canRetrieveWindowContent=false` y `canTakeScreenshot=false`.
- Observa eventos de cambio de ventana para reconocer el paquete Vivaldi, no su contenido.
- Servicio de accesibilidad con capacidad de gestos: Android mostrará una advertencia.

## Límites conocidos
- No se ha compilado ni probado en un dispositivo real en este entorno.
- El botón es un prototipo; arrastre, pulsación larga y clic pueden interferir entre sí.
- El algoritmo ejecuta como máximo 12 gestos y puede no llegar al extremo de páginas largas.
- El seguimiento de la app en primer plano mediante eventos de ventana requiere validación en Vivaldi/Android 14.
- El desplazamiento puede actuar sobre un contenedor desplazable distinto del principal.
- El botón no se oculta según la posición de scroll, sino al salir de Vivaldi.
