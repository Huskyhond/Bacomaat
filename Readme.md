Compilen van code
=================

### Webkit compilen
* Dependancy: https://github.com/mllrsohn/node-webkit-builder *
* Dependancy: https://nodejs.org/ *
1. In cmd.exe gaan we naar de map waar de webkit folder staat.
2. In cmd.exe typen we nwbuild -p win32 . -o C:/Je/Directory/Waar/Je/De/Build/Wilt/Opslaan -v v0.12.1
3. Nu kan je in de map waar het opgeslagen is de EXE file vinden

### Java Compilen
* Dependancy: Maven, deze haalt alle overige dependancy's op. Zorg dat je buiten de ingebouwde maven in je IDE ook Maven moet hebben op je PC. Dit kan je testen door mvn te typen in CMD *
1. Ga in de map van SerialEnzo met cmd.exe en typ daar in "mvn clean package" zonder quotes. 
2. Na het builden kan je in de target folder de bestanden vinden.

### Arduino Compilen
Open gewoon de Arduino Exe en load deze op je boardje. Daarna is het plug en play.

### Alles samenvoegen
1. Maak een bat aan en vul het volgende in:
```
@echo off
"MLB Banking.exe"
java -jar WebkitSerialEnzo-1.0-SNAPSHOT.jar COM3
pause
```
2. Verander eventueel de compoort nog door COM3 te veranderen in een ander nummer.
3. Sla dit op en zet dit in de build folder van Webkit.
4. Ga nu naar de gecompilde java, dit is 1 JAR file en een folder lib/ daarin staan alle dependencies.
5. Kopieer deze ook naar het webkit mapje.
6. Run de .bat file en voila.
