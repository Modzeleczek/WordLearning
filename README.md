# WordLearning
WordLearning is a native Android application for learning English vocabulary. It was created using Java, [Room (accessed 28.04.2023)](https://developer.android.com/jetpack/androidx/releases/room) for an SQLite database and [Retrofit (accessed 28.04.2023)](https://square.github.io/retrofit/) as an HTTP client. The application consists of 6 views described below.

## Application views

### Main
From here, you can navigate to the other views.

---
### Download
In this view you can download words in order to refresh your database. Everytime you click the download button, WordLearning gets some new words from [Free Random Word Generator API (accessed 28.04.2023)](https://random-word-api.vercel.app). For every generated word, the application fetches details (i.e. name of the part of speech, definition, synonyms and example of use) from [Free Dictionary API (accessed 28.04.2023)](https://dictionaryapi.dev/). The newly downloaded words replace the ones currently stored in the database. To encourage the user to learn, the application periodically sends a notification containing the number of already learned words.

---
### Learning
In `Learning` view, you locally browse your words and their details. After entering a word's details, you can click the button to quickly open the word's page in [Cambridge Dictionary](https://dictionary.cambridge.org/).

---
### Definition quiz
Everytime you open this view, if there are words in the database, a quiz attempt is generated. It consists of a word definition and four answers psuedorandomly selected from your database. Correct answers increase the learning progress of a particular word depicted by the label associated with the word in `Learning` view. A light green label near a word indicates its maximal expertise. An incorrect answer resets the progress of a word.

---
### Synonym quiz
Everytime you open this view, if there are words with synonyms in the database, a quiz attempt is generated. It consists of a word and four synonyms psuedorandomly selected from your database. In this case, correct answers do not increase the learning progress of words and incorrect ones do not reset it.

---
### Statistics
This view presents 4 counters:
- Correct definition matches - the number of correctly solved definition quiz attempts.
- Learned words - the number of words in which you have reached maximal progress.
- Correct synonym matches - the number of correctly solved synonym quiz attempts.
- Downloaded words - the total number of words downloaded everytime you clicked the download button.

---
## Running
1. Make sure that you have at least Android 11.

2. Download `WordLearning-1.0.0.apk` from the latest release in this repository.

3. If prompted by the web browser, open `WordLearning-1.0.0.apk`.

    or

    Using your file manager, locate and open `WordLearning-1.0.0.apk`.

4. In the pop-up, choose to install `WordLearning` application.

5. Open `WordLearning` from the list of installed applications.

---
## Presentation
Click the image below to watch a presentation video on YouTube.

[<img src="https://i.imgur.com/J4u3MeI.png" height="500"/>](https://youtu.be/DsQXWFWXvXQ)
