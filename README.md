# Quiz Leaderboard System — Internship Assignment

## Problem Overview

This application solves a real-world backend integration challenge where an external API (validator) provides quiz scoring data across multiple polls. The key challenge is **handling duplicate API response data correctly** to compute accurate leaderboards.

## Objective

1. Poll the validator API 10 times (calls 0–9)
2. Collect all quiz score events from responses
3. Deduplicate events using `(roundId + participant)` as the unique key
4. Aggregate total scores per participant
5. Generate a leaderboard sorted by total score (descending)
6. Compute the combined total score across all participants
7. Submit the final leaderboard to the validator API

## Solution Approach

### Key Components

**Deduplication Strategy:**
- Uses a `HashSet<String>` to track seen events by creating a composite key: `roundId + "|" + participant`
- Each event is checked against this set before aggregating scores
- Duplicate events are silently skipped with a counter for verification

**Score Aggregation:**
- Uses a `LinkedHashMap<String, Integer>` to maintain insertion order
- `scores.merge()` combines scores from multiple rounds for each participant
- Participants appearing in multiple rounds have their scores summed

**Leaderboard Generation:**
- Converts map entries to a list and sorts by score in descending order
- Generates properly formatted JSON with participant names and total scores

**API Integration:**
- Polls the endpoint 10 times with 5-second delays between requests
- Parses JSON responses using manual string extraction (no external libraries)
- Submits final leaderboard as POST request with proper JSON formatting

## How It Works

### Flow Diagram
```
1. Initialize HttpClient, empty HashSet (deduplication), empty Map (aggregation)
   ↓
2. Loop: Poll /quiz/messages with poll index 0–9
   ├─ Parse response JSON for "events" array
   ├─ Extract: roundId, participant, score
   ├─ Check: if (roundId + participant) not in seenEvents
   │   └─ Add to seenEvents + aggregate score
   ├─ Wait 5 seconds (except after last poll)
   ↓
3. Sort participants by total score (descending)
   ↓
4. Calculate combined total score
   ↓
5. POST leaderboard to /quiz/submit
   ↓
6. Display submission result
```

## Build & Run

### Prerequisites
- Java 21+ (or Java 25+)
- Git

### Compilation
```bash
javac src/com/quiz/QuizLeaderboard.java
```

### Execution
```bash
java -cp src com.quiz.QuizLeaderboard
```

The program will:
- Execute 10 API calls with 5-second delays (~50 seconds total)
- Display each response and deduplication counts
- Print the final aggregated scores
- Submit the leaderboard and show the result

### Update Registration Number
Edit `src/com/quiz/QuizLeaderboard.java` and update:
```java
private static final String REG_NO = "RA2311047010037";
```

## Example Output

```
=== Starting Quiz Leaderboard System ===

Polling 0/9...
  Response: {...}
  Processed: 3 new, 0 duplicates skipped
  Waiting 5 seconds...

Polling 1/9...
  Response: {...}
  Processed: 2 new, 1 duplicate skipped
  Waiting 5 seconds...

...

=== Aggregated Scores ===
  Alice: 100
  Bob: 120

Total score: 220

=== Submitting Leaderboard ===
{
  "regNo": "RA2311047010037",
  "leaderboard": [
    {"participant": "Bob", "totalScore": 120},
    {"participant": "Alice", "totalScore": 100}
  ]
}

=== Submission Result ===
{"isCorrect": true, "isIdempotent": true, "submittedTotal": 220, "expectedTotal": 220, "message": "Correct!"}
```

## Duplicate Handling Example

```
Poll 0: { roundId: "R1", participant: "Alice", score: 10 }  → Key: "R1|Alice" → Added
Poll 1: { roundId: "R1", participant: "Alice", score: 10 }  → Key: "R1|Alice" → Skipped (duplicate)
Poll 2: { roundId: "R2", participant: "Alice", score: 15 }  → Key: "R2|Alice" → Added

Final: Alice = 10 + 15 = 25
```

## API Details

**Base URL:** `https://devapigw.vidalhealthtpa.com/srm-quiz-task`

**GET /quiz/messages**
- Query Params: `regNo`, `poll` (0–9)
- Returns: List of quiz events with roundId, participant, score

**POST /quiz/submit**
- Payload: `{ "regNo": "...", "leaderboard": [...] }`
- Returns: `{ "isCorrect": true/false, "submittedTotal": ..., "expectedTotal": ..., "message": "..." }`

## Requirements Checklist

- ✅ 10 polls executed (indices 0–9)
- ✅ Duplicate API response data handled using (roundId + participant) composite key
- ✅ Leaderboard sorted by total score (descending)
- ✅ Total score calculated correctly
- ✅ Submission executed once
- ✅ Public GitHub repository with code
- ✅ Detailed README documentation
