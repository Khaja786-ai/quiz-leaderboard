# Quiz Leaderboard System — Internship Assignment

A Java application that polls a validator API 10 times, deduplicates quiz score events, aggregates participant scores, and submits a leaderboard.

## Build & Run

### Prerequisites
- Java 21+ (or Java 25+)

### Compilation
```bash
javac src/com/quiz/QuizLeaderboard.java
```

### Execution
```bash
java -cp src com.quiz.QuizLeaderboard
```

Execution time: ~50 seconds (10 polls × 5-second delays)

## Output

```
=== Starting Quiz Leaderboard System ===

Polling 0/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":0,"events":[{"roundId":"R1","participant":"Alice","score":10},{"roundId":"R1","participant":"Bob","score":20}]}
  Processed: 2 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 1/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":1,"events":[{"roundId":"R2","participant":"Alice","score":15},{"roundId":"R1","participant":"Alice","score":10}]}
  Processed: 1 new, 1 duplicate skipped
  Waiting 5 seconds...

Polling 2/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":2,"events":[{"roundId":"R2","participant":"Bob","score":25}]}
  Processed: 1 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 3/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":3,"events":[{"roundId":"R1","participant":"Bob","score":20}]}
  Processed: 0 new, 1 duplicate skipped
  Waiting 5 seconds...

Polling 4/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":4,"events":[]}
  Processed: 0 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 5/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":5,"events":[{"roundId":"R3","participant":"Alice","score":20}]}
  Processed: 1 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 6/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":6,"events":[{"roundId":"R3","participant":"Bob","score":25}]}
  Processed: 1 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 7/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":7,"events":[]}
  Processed: 0 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 8/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":8,"events":[]}
  Processed: 0 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 9/9...
  Response: {"regNo":"RA2311047010037","setId":"SET_1","pollIndex":9,"events":[]}
  Processed: 0 new, 0 duplicates skipped

=== Aggregated Scores ===
  Alice: 45
  Bob: 70

Total score: 115

=== Submitting Leaderboard ===
{
  "regNo": "RA2311047010037",
  "leaderboard": [
    {"participant": "Bob", "totalScore": 70},
    {"participant": "Alice", "totalScore": 45}
  ]
}

=== Submission Result ===
{"isCorrect": true, "isIdempotent": true, "submittedTotal": 115, "expectedTotal": 115, "message": "Correct!"}
```
