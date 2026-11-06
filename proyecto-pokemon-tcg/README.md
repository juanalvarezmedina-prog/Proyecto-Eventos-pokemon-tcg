## PROYECTO POKEMON TCG

INTEGRANTES

## Juan Camilo Alvarez Medina - 2569416

## Joan Manuel Arcila Cano - 2569151

## Juan Jose Cardona Rodriguez - 2569066

En este proyecto se hará una version del juego para moviles Pokemon TCG, esta versión (mp1) se abarcará todo lo que tiene que ver con la logica interna del juego, creación de clases y cómo se conectan, así como también una simulación del juego por consola.

El juego inicia cuando los jugadores escriben su nombre y eligen un mazo predefinido de 20 cartas (Fuego, Agua o Eléctrico). Al iniciar, el jugador que empiece no podrá atacar en el primer turno, sin embargo, podrá hacer uso del resto de funciones. Luego de esto, se iniciará un bucle de turnos en donde cada jugador tratará de ganar según su estrategia. Cada jugador podrá robar una carta, jugar entrenadores, atacar, bajar Pokémon al banco o retirar el activo y en cada turno se le otorgará una energía del tipo al que pertenezca el mazo correspondiente. Para ganar un jugador deberá conseguir tres puntos de premio (obtenidos por derrotar un Pokémon) o bien, si el rival no puede robar más cartas o si se queda sin Pokémon en juego.
Cómo ejecutar el juego


Requisitos

Java 21 o superior (JDK)
No se necesitan librerías externas, solo Java estándar.

### Pasos
1. Clona el repositorio y entra a la carpeta:
   ```bash
   git clone https://github.com/juanalvarezmedina-prog/Proyecto-Eventos-pokemon-tcg.git
   cd Proyecto-Eventos-pokemon-tcg
   ```
2. Compila todas las clases (desde la raíz del repositorio):
   ```bash
   javac -encoding UTF-8 -d out proyecto-pokemon-tcg/src/*.java
   ```
3. Ejecuta el juego:
   ```bash
   java -Dfile.encoding=UTF-8 -cp out App
   ```