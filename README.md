# Medtracker

**Medtracker** est une application Android destinée au suivi de la médication. Elle permet de scanner des codes DataMatrix de médicaments, de planifier des prises, de recevoir des rappels et de consulter l'historique des prises.

---

## ✨ Fonctionnalités principales

- **Scan de codes DataMatrix**  
  Scannez le code DataMatrix d'un médicament à l'aide de la caméra afin d'enregistrer une prise.

- **Planification des prises**  
  Ajoutez des plans de médication : sélectionnez un code scanné et programmez une date et une heure de rappel.

- **Notification de rappel**  
  L'application envoie une notification à l'heure prévue pour rappeler la prise du médicament.

- **Validation par scan**  
  Pour valider une prise, il est nécessaire de rescanner le code DataMatrix du médicament.

- **Historique des prises**  
  Consultez l'ensemble des prises enregistrées, avec la date et le code associé.

- **Mode sombre/clair**  
  L'interface s'adapte automatiquement au mode d'affichage du système Android.

- **Suppression rapide**  
  Un bouton flottant permet de supprimer l'ensemble des plans de médication.

---

## 🚀 Installation et utilisation

### 1. Clonage du projet
```bash
git clone https://github.com/<Gavv06>/<medtracker>.git
cd <medtracker>
```

### 2. Ouverture du projet
- Ouvrez le dossier du projet à l'aide d'**Android Studio** (logiciel gratuit, recommandé pour le développement Android).  
- Pour ce faire une fois Android studio ouvert allez dans file/new/Project from version control et copier l'url de ce repo (en s'assurant d'avoir bien choisi Github)  
- [Télécharger Android Studio](https://developer.android.com/studio)

### 3. Lancement de l'application
- Cliquez sur le bouton « Run » (triangle vert) dans Android Studio pour exécuter l'application sur un émulateur ou un appareil Android connecté.

### 4. Utilisation de l'APK fourni
- Un fichier **APK de debug** est disponible dans `app/build/outputs/apk/debug/app-debug.apk`.
- Il peut être installé directement sur un appareil Android (via un gestionnaire de fichiers ou la commande `adb install`).

---

## 📦 Différence entre APK debug et APK release

- **APK debug**  
  - Destiné aux tests sur un appareil ou un émulateur.
  - Non optimisé, signé avec une clé générique.
  - Ne peut pas être publié sur le Play Store.

- **APK release (signé)**  
  - Destiné à la publication ou à la distribution officielle de l'application.
  - Optimisé, signé avec une clé privée propre au développeur.
  - Obligatoire pour la publication sur le Play Store.

> **L'APK fourni ici est un APK debug, destiné uniquement à des fins de test.  
> Pour publier l'application, il convient de générer un APK release signé (voir instructions ci-dessus).**

---

## 📝 Prérequis

- **Android Studio** (Windows, Mac, Linux)
- **Android 8.0 (API 26)** ou version ultérieure recommandée pour l'appareil cible

---

## 📷 Permissions requises

- **Caméra** : pour scanner les codes DataMatrix
- **Notifications** : pour recevoir les rappels de prise

---

## 🎨 Icône de l'application

> **Remarque : l'application ne dispose pas d'une icône personnalisée par défaut.**

### Procédure pour ajouter une icône :
1. Préparez une image carrée (idéalement 512x512 px, au format PNG ou SVG, avec un fond transparent ou coloré).
2. Dans Android Studio, effectuez un clic droit sur le dossier `res` > **New > Image Asset**.
3. Sélectionnez **Launcher Icons (Adaptive and Legacy)**.
4. Importez votre image dans la section « Foreground Layer ».
5. Cliquez sur **Next** puis **Finish**.
6. Android Studio générera automatiquement toutes les tailles nécessaires dans `res/mipmap-*`.

**Astuce :** Il est possible d'utiliser [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html) pour générer une icône à partir d'un fichier SVG ou PNG.

---

## 💡 Conseils d'utilisation

- Pour tester la notification, planifiez une prise dans les minutes à venir.
- Utilisez le bouton « Vider le plan de médication » pour réinitialiser la liste des plans.

---

## 📄 Licence

Projet open source, destiné à un usage éducatif ou dans le domaine de la santé.

--- 
