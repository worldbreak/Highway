Formát dat z portálù na SOKP
----------------------------

Podadresáøe oznaèují stanièení portálù, prozatím je k dispozici pouze
jejich podmnožina. Stanièení je va stovkách metrù, adresáø `0218` tedy
obsahuje data z portálu na kilometru 21.8

Polohu portálù SOKP lze najít na Google Maps:
http://maps.google.com/maps/ms?ie=UTF8&hl=cs&oe=UTF8&msa=0&msid=212348345266659778150.00049b4aead9e3d8193c6

V každém podadresáøi portálu se pak nacházejí .mat soubory s jednodenními
daty z daného portálu, formát názvu souboru je

	sokp-cccc-yyyymmdd.mat

kde `cccc` je opìt stanièení ve stovkách metrù a `yyyymmdd` je datum.

Formát dat v souboru
--------------------

Po naètení pøíkazem load() je k dispozici struktura `data` s následujícími
položkami:

.date ......... opìt datum v anglosaském formátu 'yyyy-mm-dd'

.time_step .... vzorkovací krok v sekundách, pro minutová data je to 60

.gantry_id .... ještì jednou stanièení ve stovkách metrù, pro soubory
                v adresáøi `0218` je to tedy vždy 218

.lanemap ...... identifikátory jízdních pruhù, zastoupených v datech, dle
                ponìkud specifického znaèení pøedpisem EDS - zevnitø postupnì
                0,6,4,2 pro portály "vlevo", respektive 9,5,3,1 pro portály
                "vpravo" v pøípadì tøípruhu (znaèení je specifické tím, že
                používá 1/2 pro nouzový pruh, 3/4 pro vnìjší resp. pøídavný
                pruh, 5/6 pro støední jízdní pruh je, a 9/0 pro vnitøní jízdní
                pruhy); v pøípadì dvojpruhu vypadnou pruhy 5/6

.los .......... matice s údaji LOS (level-of-service) pro jednotlivé detektory
                øádky jsou jednotlivé vzorky, sloupce odpovídají jízdním pruhùm
                podle vektoru `lanemap`; pro minutové vzorkování a dva jízdní
                pruhy je to matice 1440x2
                
.cnt .......... poèet vozidel
.occ .......... obsazenost
.spd .......... rychlost
                vše uloženo jako cell vektory obsahující matice pro celkem
                devìt kategorií vozidel ve formátu stejném, jako je LOS;
                kategorie jsou následující:
                0 ... ostatní
                1 ... motocykl
                2 ... osobní auto
                3 ... osobní auto s pøívìsem
                4 ... dodávka
                5 ... nákladní auto
                6 ... nákladní auto s pøívìsem
                7 ... kamión
                8 ... autobus
                9 ... všechna vozidla dohromady
                                     
