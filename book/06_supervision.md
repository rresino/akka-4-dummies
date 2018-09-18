# Supervisión

## Jerarquía de actores

Todos los actores pertenecen a una jerarquía de actores en la unos actores pertenecen a un padre hasta llegar a `root guardian` o actor raíz. De ese actor solo dependen 2 actores `guardian` y `system guardian`. 

- `/` o `root guardian` : Es el actor padre de todos. No es un actor real pero se le pueden asignar políticas de supervisión para que el resto de actores la hereden automáticamente.
- `/user` o `guardian` : Es el guardian de todos los actores creados por el usuario.
- `/system` o `guardian` : Es el guardian de todos los actores internos o del sistema.
- `/deadLetters` : Es el actor encargado de recibir todos esos mensajes perdidos o que no son procesados por ningún actor.
- `/temp` : Es el padre de aquellos actor de sistema con un tiempo de vida muy corto.
- `/remote` : Realmente no es un actor sino un path artificial para referirse a la referencias de los actores que tiene un supervisor con una referencia a un actor remoto.

> Realmente con tener claro los 3 primeros actores es suficiente, el resto son para casos avanzados.

## Supervisión 

Todos los actores son susceptibles de fallar (ejemplo: perder conexión con la base de datos y lanzar una excepción), y entonces entra en acción el supervisor del actor para decidir que hacer en caso de fallo. El supervisor es el actor padre de actor.

Los actores tienen varios métodos que se pueden utilizar para realizar acciones ante determinados eventos:

- **PreStart:** fase previa a iniciarse un actor, pero no se ejecuta al resetearse el actor. Esta fase es ideal para inicializar lo que se necesite.
  
- **PostStop:** fase previa a la parada controlada de un actor, pero no por un fallo.

- **PreRestart:** fase previa a un reinicio del actor tras un fallo.

- **PostRestart:** fase posterior a un reinicio del actor tras un fallo. Es util para reinicializar lo necesario.

- **Unhandled:** método que se ejecuta cada vez que el actor (vivo) recibe un mensaje que no se procesa. Muy recomendado utilizar para logar mensajes perdidos y darse cuenta que se están perdiendo.

- **SupervisorStrategy:** Define la estrategia de supervisión de los actores hijos. Sino se define una estrategia hereda la del padre. Ver "Como definir una Estrategia de Supervisión"

## Como definir una Estrategia de Supervisión

Como ya hemos visto para establecer una estrategia de supervisión hay que sobrescribir el método `supervisorStrategy`:

```scala
override def supervisorStrategy: SupervisorStrategy = ???
```

### Tipos de estrategias:

- **Por defecto**: por defecto según el tipo de excepción :
    - ActorInitializationException: Para el actor.
    - ActorKilledException: Para el actor.
    - DeathPactException: Para el actor.
    - Exception: Resetea el actor.
    - Para el resto aplica la estrategía del padre (escalate).
  
- **OneForOneStrategy:** Se define un estrategia para el actor que falla unicamente. Define el máximo de ejecuciones en determinado tiempo y que hacer en función de cada acción. Puedes poner -1 para indicar que no hay limite, en uno o los 2 parámetros.
  ```scala
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 1 second) {
      case e: UnsupportedOperationException => Restart
      case _ => Stop
    }
  ```

- **AllForOneStrategy:** Funciona igual que `OneForOneStrategy` pero aplica a todos los actores hijos, al contrario que el anterior que aplica solo a el mismo. Es un poco extraño al principio pero puede ser interesante reiniciar todos los actores hijos ante un determinado fallo.

### Acciones

- **Resume**: Ignora el error y continua. Mucho cuidado con esto porque puede esconder problemas potenciales.
- **Stop**: Para el actor.
- **Restart**: Resetea el actor.
- **Escalate**: Escala la supervisión al padre para que aplique su política de supervisión.

### Ejemplos

- **Ejemplo LifeCycleActor**: Ejemplo en el que se muetra como un actor padre generá 3 hijos mediante mensajes `ask` que ejecutarse todos los futuros empieza a enviarle mensajes para mostrar como se reinician todos los actores hijos al enviar un fallo leve, posteriormente como se paran los 3 actores al provocar un fallo grave. Es muy interesante ver como se pintan todas las trazas de Restart y PostStop al pararse. Puedes probar a cambiar el tipo de estrategia ver que pasa.

Este es el código de [ejemplo completo](../src/main/scala/com/rresino/akka4dummies/c06/LifeCycleActor.scala).

--- 

- Siguiente [????](./03_???.md)
- Volver a [Enviando mensajes](./05_msgs.md)
- [Ir al Inicio](../README.md) 