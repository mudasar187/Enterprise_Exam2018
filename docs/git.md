[Back to README.md](../README.md)

# Git Structure

## Branches

- Master
- Dev
- release
  - release-0.1
  - release-0.2
  - etc...
- feature
  - feature-authentication
  - feature-front-end-login
  - etc..

##### Feature branches
For hver nye feature så kan vi lage en egen branch. Et eksempel kan være å implementere spring security eller lage login funksjonalitet i frontend.

Grunnlaget for dette er for å enklere kunne jobbe med flere ting samtidig, dersom f.eks. du må hoppe over fra å jobbe med autentisering til å gjøre en oppdatering i en service klasse eller liknende. Dette gjør det og enklere for andre å hoppe inn i andre sine brancher og eventuelt ta over kode.

##### Release branches
Når vi er ferdig med en feautre kan vi merge dette inn i en release branch, her kan den ligge til alle features som vi har bestemt skal være med i en release er klar.

##### Dev
Når alle features er merget inn i en release og alle conflicts er løst kan dette pushes inn i dev. Når kode pushes inn i dev vil Travis plukke opp koden og kjøre et bygg på den nye koden.

##### Master
Master skal ALLTID være stabil. For å kunne få koden inn i Master banch må den gjennom en pull request, her må minst en annen person gjøre et code review av koden før den får lov til å gå inn i Master.