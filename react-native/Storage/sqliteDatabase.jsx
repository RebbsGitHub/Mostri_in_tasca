import React, { createContext, useEffect, useState } from 'react';
import * as SQLite from 'expo-sqlite';
import { SQLiteProvider, useSQLiteContext } from 'expo-sqlite';


// Funzione chiamata

export const setupTables = async (db) => {

    createUserTables(db)
    createVOBJTables(db)
    console.log('spero tabelle entrambe create')
}

// Inizializzazione dei Database

// Players Profiles
export const createUserTables = async (db) => {
    try{
        await db.execAsync(
            `CREATE TABLE IF NOT EXISTS users (
            uid INTEGER PRIMARY KEY,
            name TEXT,
            life INTEGER, 
            experience INTEGER,
            weapon INTEGER,
            armor INTEGER,
            amulet INTEGER,
            picture STRING, 
            profileversion INTEGER,
            positionshare STRING
            );`
        );
        console.log('database Users init!');
    }catch (error){
        console.log('Errore User Db: ',error);
    }
};
// Virtual OBJ
export const createVOBJTables = async (db) => {
    try {
        await db.execAsync(
            `CREATE TABLE IF NOT EXISTS virtualobj (
                id INTEGER PRIMARY KEY,
                type TEXT,
                level INTEGER, 
                lat FLOAT,
                lon FLOAT,
                image STRING,
                name TEXT
                );`
        );
        console.log('database virtualobj init!');
    } catch (error) {
        console.log('Errore virtualobj Db: ', error);
    }
};


//--------------------------------------Funzioni per Gli Users---------------------------------------------------

//Aggiornamento database Users - V
    export const addUser = async (item, db) => {
        try {
                console.log("Inserimento dati in User...")
                await db.runAsync('INSERT INTO users (uid,name,life,experience,weapon,armor,amulet,picture,profileversion,positionshare) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [item.uid, item.name, item.life, item.experience, item.weapon ,item.armor ,item.amulet ,item.picture , item.profileversion, item.positionshare]);
                console.log("Inserimento dati in User... FATTO")
        } catch (error) {
            console.log("ERRORE Inserimento in User: ",error)
        }
    }


    //Aggiornamento database Users - V
    export const deleteAllUsers = async (db) => {
        async function cancellaUsers(db) {
            try {
                const valueret = await db.runAsync('DROP TABLE IF EXISTS users;',);
                console.log('cancellazione database eseguita, ', await valueret)

            } catch (error) {
                console.log('ERRORE caricamento funzione getAllUsers : ', error);
            }
        };
        cancellaUsers(db);
} 


// Richiesta TUTTI gli User - V
    export const getAllUsers = async (db) => {
        async function getUsers(db) {
            try {
                const allRows = await db.getAllAsync('SELECT * FROM users');
                console.log(' dati:')
                if (!allRows) {
                    console.log("non ci sono entry nel db")
                } else {
                    for (const row of allRows) {
                    console.log(row.uid, row.life, row.experience);
                    }
                }
               
            } catch (error) {
                console.log('ERRORE caricamento funzione getAllUsers : ', error);
            }
        };
        getUsers(db);
    } 


// Richiesta User tramite UID -V
    export const getUserByUid = async (db, uidProfile) => {
        async function getUsers(db, uidProfile) {
            try {
                const Row = await db.getFirstAsync('SELECT * FROM users WHERE uid = ?', uidProfile);
                //console.log(' dati:')
                if (!Row) {
                    //console.log("non cè il tizio nel db")
                    return null
                } else {
                    //console.log(Row.uid, Row.life, Row.experience);

                    return Row;
                } 

            } catch (error) {
                console.log('ERRORE caricamento funzione getUserByUid : ', error);
            }
        };
    
    return getUsers(db, uidProfile);
    } 



// Replace User - non ancora testata, necessario aspettare di ammazzare i mostri
    export const replaceUser = async (db, profile) => {

        async function deleteSingleUserById(db, uidProfile) {
            try {
                await db.runAsync('DELETE FROM users WHERE uid = ?', uidProfile);
                return true;

            } catch (error) {
                console.log('ERRORE in deleteSingleUserById : ', error);
                return false;
            }
        };

        async function thenAddTheUser(db, item) {
            try {
                await db.runAsync('INSERT INTO users (uid,name,life,experience,weapon,armor,amulet,picture,profileversion,positionshare) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [item.uid, item.name, item.life, item.experience, item.weapon, item.armor, item.amulet, item.picture, item.profileversion, item.positionshare]);
                return true;
            } catch (error) {
                console.log("ERRORE Inserimento thenAddTheUser: ", error)
                return false;
            }
        };
        
        
        if(await deleteSingleUserById(db,profile.uid) === true){
            let resultofchanges = await thenAddTheUser(db, profile)
            return resultofchanges;
        }

    } 




//--------------------------------------Funzioni per Gli oggetti virtuali---------------------------------------------------


//Inserimento nuovo Item - V
export const addItem = async (item, db) => {
    try {
        console.log("Inserimento dati in virtualobj...")
        await db.runAsync('INSERT INTO virtualobj (id,type,level,lat,lon,image,name) VALUES (?, ?, ?, ?, ?, ?, ?)', [item.id, item.type, item.level, item.lat, item.lon, item.image, item.name]);
        console.log("Inserimento dati in virtualobj... FATTO")
    } catch (error) {
        console.log("ERRORE Inserimento in addItem per virtualobj: ", error)
    }
}


//Aggiornamento database, rimozione dati Items - (ancora da provare)
export const deleteAllvirtualobj = async (db) => {
    async function cancellaItems(db) {
        try {
            const valueret = await db.runAsync('DROP TABLE IF EXISTS virtualobj;',);
            console.log('cancellazione database virtualobj eseguita, ', await valueret)

        } catch (error) {
            console.log('ERRORE caricamento funzione deleteAllvirtualobj per  virtualobj : ', error);
        }
    };
    cancellaItems(db);
}


// Richiesta Item tramite ID - V
export const getItemById = async (db, idItem) => {
    async function getItem(db, idItem) {
        try {
            const Row = await db.getFirstAsync('SELECT * FROM virtualobj WHERE id = ?', idItem);
            //console.log(' dati:')
            if (!Row) {
                console.log("non cè l oggettino nel db")
                return null
            } else {
                console.log('Trovato l oggettino!');
                return Row;
            }

        } catch (error) {
            console.log('ERRORE caricamento funzione getItemById pre virtualobj : ', error);
        }
    };

    return getItem(db, idItem);
}