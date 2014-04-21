Form�t dat z port�l� na SOKP
----------------------------

Podadres��e ozna�uj� stani�en� port�l�, prozat�m je k dispozici pouze
jejich podmno�ina. Stani�en� je va stovk�ch metr�, adres�� `0218` tedy
obsahuje data z port�lu na kilometru 21.8

Polohu port�l� SOKP lze naj�t na Google Maps:
http://maps.google.com/maps/ms?ie=UTF8&hl=cs&oe=UTF8&msa=0&msid=212348345266659778150.00049b4aead9e3d8193c6

V ka�d�m podadres��i port�lu se pak nach�zej� .mat soubory s jednodenn�mi
daty z dan�ho port�lu, form�t n�zvu souboru je

	sokp-cccc-yyyymmdd.mat

kde `cccc` je op�t stani�en� ve stovk�ch metr� a `yyyymmdd` je datum.

Form�t dat v souboru
--------------------

Po na�ten� p��kazem load() je k dispozici struktura `data` s n�sleduj�c�mi
polo�kami:

.date ......... op�t datum v anglosask�m form�tu 'yyyy-mm-dd'

.time_step .... vzorkovac� krok v sekund�ch, pro minutov� data je to 60

.gantry_id .... je�t� jednou stani�en� ve stovk�ch metr�, pro soubory
                v adres��i `0218` je to tedy v�dy 218

.lanemap ...... identifik�tory j�zdn�ch pruh�, zastoupen�ch v datech, dle
                pon�kud specifick�ho zna�en� p�edpisem EDS - zevnit� postupn�
                0,6,4,2 pro port�ly "vlevo", respektive 9,5,3,1 pro port�ly
                "vpravo" v p��pad� t��pruhu (zna�en� je specifick� t�m, �e
                pou��v� 1/2 pro nouzov� pruh, 3/4 pro vn�j�� resp. p��davn�
                pruh, 5/6 pro st�edn� j�zdn� pruh je, a 9/0 pro vnit�n� j�zdn�
                pruhy); v p��pad� dvojpruhu vypadnou pruhy 5/6

.los .......... matice s �daji LOS (level-of-service) pro jednotliv� detektory
                ��dky jsou jednotliv� vzorky, sloupce odpov�daj� j�zdn�m pruh�m
                podle vektoru `lanemap`; pro minutov� vzorkov�n� a dva j�zdn�
                pruhy je to matice 1440x2
                
.cnt .......... po�et vozidel
.occ .......... obsazenost
.spd .......... rychlost
                v�e ulo�eno jako cell vektory obsahuj�c� matice pro celkem
                dev�t kategori� vozidel ve form�tu stejn�m, jako je LOS;
                kategorie jsou n�sleduj�c�:
                0 ... ostatn�
                1 ... motocykl
                2 ... osobn� auto
                3 ... osobn� auto s p��v�sem
                4 ... dod�vka
                5 ... n�kladn� auto
                6 ... n�kladn� auto s p��v�sem
                7 ... kami�n
                8 ... autobus
                9 ... v�echna vozidla dohromady
                                     
