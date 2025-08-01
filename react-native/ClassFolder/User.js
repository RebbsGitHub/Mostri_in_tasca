class User {


    constructor(uid, name, life, experience, weapon, armor, amulet, picture, profileversion, positionshare) {
        this.uid = uid;
        this.name=name;
        this.life=life;
        this.experience=experience;
        this.weapon=weapon;
        this.armor=armor;
        this.amulet=amulet;   
        this.picture=picture; 
        this.profileversion=profileversion;
        this.positionshare=positionshare;

}

    

    
    /*

    // Metodo per ottenere il nome completo dell'utente
    getNomeCompleto() {
    return `${this.nome} ${this.cognome}`;
    }

    // Metodo per calcolare l'età dell'utente in base alla data attuale
    calcolaEta() {
    const oggi = new Date();
    let eta = oggi.getFullYear() - this.dataDiNascita.getFullYear();
    const mese = oggi.getMonth() - this.dataDiNascita.getMonth();

    // Controlla se non è ancora passato il compleanno quest'anno
    if (mese < 0 || (mese === 0 && oggi.getDate() < this.dataDiNascita.getDate())) {
    eta--;
    }
    return eta;
    }

    // Metodo per aggiornare il nome dell'utente
    setNome(nome) {
    this.nome = nome;
    }

    // Metodo per aggiornare il cognome dell'utente
    setCognome(cognome) {
    this.cognome = cognome;
    }
*/

}

export default User;