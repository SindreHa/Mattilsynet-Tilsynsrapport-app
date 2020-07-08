# Applikasjonsutvikling eksamen 2019 · [![Version](https://img.shields.io/badge/Version-1.1-Green.svg)](https://shields.io/) [![Maintenance](https://img.shields.io/badge/Maintained%3F-no-red.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity) [![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/Naereen/StrapDown.js/blob/master/LICENSE)
Min eksamen i emnet Applikasjonsutvikling for mobile enheter på Universitetet i Sørøst-Norge
## Oppgaven
- Du skal lage en app som kan vise resultater fra Mattilsynets tilsyn av serveringssteder. Appen skal
basere seg på åpne data fra Mattilsynet som er tilgjengelig fra et REST-API hos Difis Datahotell på
data.norge.no.
### Krav til funksjonalitet
- [x] Finne tilsyn for et spisested via navn og eller poststed
- Bruker skal kunne søke etter spisesteder (tilsyn) basert på en kombinasjon av navn på spisestedet og
eller navn på poststed i datasettet tilsyn. 
- [x] Vise detaljinformasjon om et tilsyn
- Når bruker velger en rad (spisested) fra listen skal det vises et nytt skjermbilde med all relevant
informasjon fra datasettet Tilsyn.
- En del av skjermen skal også vise en liste
med alle kravpunktene for det aktuelle tilsynet
- [x] Tilpasse søkelisten for tilsyn
- Bruker skal kunne filtrere søkelisten basert på årstall for tilsynet, f.eks. kunne velge alle, eller ett
årstall.
- [x] Finne tilsyn/spisesteder basert på brukers posisjon (geografisk søk)
- Appen skal også ha en funksjon for å vise en liste med alle tilsyn/spisesteder som er registrert på det
postnummeret der brukeren befinner seg
- [x] Innstillinger / Settings / brukervalg
- App’en skal ha et valg for Innstillinger (Settings) der bruker kan legge inn noen faste opplysninger
som lagres lokalt mellom hver programkjøring, f.eks. et "favorittsted" (poststed eller postnummer) og
evt. fast årstall for filtrering av Tilsyn.

### Hvordan kjøre
Appen er laget i [AndroidStudio](https://developer.android.com/studio) og kan derfra kjøres via enten en emulator eller en fysisk Android enhet. Appen kan kjøres med Oreo (8.0) og oppover.

### Skjermbilder av applikasjon
![skjermbilder](https://i.imgur.com/kQQGyPc.jpg)

