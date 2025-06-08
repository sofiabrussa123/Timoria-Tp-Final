# Nombre del Proyecto: Timōría

## Integrantes del Grupo
- Sofía Brussa  
- Enzo Stigliano

## Descripción
**Timōría** es un videojuego de plataformas 2D con fuerte carga narrativa, ambientado en el inframundo griego.  
Los jugadores encarnan almas en busca de redención, enfrentando desafíos cooperativos y competitivos a través de niveles simbólicos inspirados en emociones humanas. A lo largo del camino, deben resolver acertijos, combatir enemigos mitológicos y recolectar dracmas para progresar en habilidades, narrativa y equipamiento.

## Tecnologías Utilizadas
- **Lenguajes**: Java
- **Motor**: LibGDX  
- **Extensiones**: Box2D, Box2DLights, FreeType, VisUI, AI, WebSocket, makeSomeNoise  
- **Plataformas Objetivo**: Escritorio (Windows/Linux/macOS)

## Requisitos
- Tener instalado **JDK 8 o superior**  
- Tener instalado **git**  
- Tener una cuenta de **GitHub**

## Wiki del proyecto
https://github.com/sofiabrussa123/Timoria-Tp-Final/wiki

## Cómo Instalar, Compilar, y Ejecutar

1. Asegurate de tener **Eclipse IDE for Java Developers** y el plugin **Buildship: Gradle Integration** instalado.

2. Abra una consola de comandos:
   - Si está en Windows, busque "cmd"; si está en MacOS o Linux busque "terminal"
   - Escriba git clone https://github.com/sofiabrussa123/Timoria-Tp-Final
   - Escriba cd Timoria-Tp-Final

3. Abrí Eclipse, luego:  
   - Ir a `File > Import...`  
   - Elegí `Gradle > Existing Gradle Project`  
   - Seleccioná la carpeta raíz del juego  
   - Terminá los pasos que te diga el asistente.
4. Esperá a que Eclipse sincronice el proyecto y descargue las dependencias.
5. En el módulo `desktop`, buscá la clase:  
   `com.timoria.game.desktop.Lwjgl3Launcher`
6. Hacé clic derecho sobre `Lwjgl3Launcher.java` > `Run As` > `Java Application`
7. *(Opcional)* Si el juego no carga correctamente, verificá la siguiente configuración:  
   - Hacé clic derecho en la clase > `Run Configurations...`  
   - En la pestaña **Arguments**, sección **Working Directory**, seleccioná:  
     `Other > Browse Workspace... >` Elegí la carpeta `assets` dentro del módulo `core`.

## Estado Actual del Proyecto
Configuración inicial y estructura del proyecto.
