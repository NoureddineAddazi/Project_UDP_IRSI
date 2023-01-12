********** Projet : Chat (Datagram UDP Java)**********

C'est une application de chat client-serveur où la partition de projet contient deux classes Java :
----------------------------------------------------------------------------------------------------------
#Classe ChatClient :
----------------------------------------------------------------------------------------------------------
=>demander à un client à entrer son identifiant
=>vérifier si son identifiant entré existe à la BD
     **si oui, demander au client d'entrer le username de destinateur puis le message à envoyer
     **sinon, demander au client d'entrer un autre identifiant qui est valable
=>inclure le nom d'utilisateur et le destinataire dans le message
=>envoyer le message (username + destinateur + messageEcrit) au serveur pour
le diviser et extraire le messageEcrite et le destinateur pour envoyer le messageEcrit au destinataire
----------------------------------------------------------------------------------------------------------
#Classe ChatServer :
----------------------------------------------------------------------------------------------------------
=>recevoir le message envoyé par le client
=>diviser le message et extraire le messageEcrite et le destinateur pour envoyer le messageEcrit au destinataire
=>vérifier si l'identifiant de client entré existe à la BD
     **si oui, enregistrer l'identifiant dans une liste de type Map des utilisateurs connectés
     **sinon, demander au client d'entrer un idantifient valide
=>vérifier si l'identifiant de destinataire entré existe dans la liste Map des utilisateurs connectés
     **si oui, demander au client d'entrer un message
     **sinon, demander au client d'entrer l'identifiant d'un destinataire qui est connecté
=>envoyer le messageEcrit au destinataire cible


///////////////////////////////*****Base de données*****//////////////////////////////////////

Pour la base de données "etudiants" il y a une table "etudiant" qui contient les données
des étudiants IRSI (cne,nom,prénom) .
(Juste les étudiants qui sont enregistrés dans la table "etudiant" qui veulent se connecter).

/////////////////////////////////////////////////////////////////////////////////////////////