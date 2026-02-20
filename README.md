# Softmax Action Selection Based Load Balancer

## 📌 Project Description

This project implements a client-side adaptive load balancer using the **Softmax Action Selection algorithm**.

The system distributes incoming requests across K servers whose response times are:

- Non-stationary (change over time)
- Noisy (contain Gaussian noise)

The objective is to minimize total latency and maximize overall reward.

---

## 🎯 Problem Definition

Given K servers in a cluster:

- Each server has dynamic and time-varying latency.
- Classical algorithms such as Random or Round-Robin do not adapt to performance changes.
- We design an adaptive algorithm that learns from past observations.

Goal:

Minimize:

Total Latency

Maximize:

Total Reward

---

## 🧠 Compared Algorithms

### 1️⃣ Random Selection
Selects a server randomly.

- No learning
- No adaptation
- Time Complexity: O(1)

---

### 2️⃣ Round-Robin
Selects servers sequentially.

- Equal traffic distribution
- Ignores performance differences
- Time Complexity: O(1)

---

### 3️⃣ Softmax Action Selection (Proposed Method)

Each server has an estimated value:

Q_i = Average reward of server i

Selection probability:

P(i) = exp(Q_i / τ) / Σ exp(Q_j / τ)

Where:

- τ (Temperature) controls exploration-exploitation tradeoff.

Smaller τ → more greedy  
Larger τ → more exploration  

Time Complexity per selection: O(K)

---

## ⚠ Numerical Stability Solution

The exponential function may cause overflow when Q values are large.

To prevent this, we use:

exp((Q[i] - maxQ) / τ)

This ensures numerical stability.

---

## 🔁 Learning Rule

Reward is defined as:

Reward = 1000 / Latency

Update rule (Sample Average Method):

Q_new = Q_old + (1 / N) * (Reward - Q_old)

Where:
- N is the number of times the server has been selected.

---

## 🌊 Non-Stationary Environment

Server performance drifts over time:

serverCurrentMeans[server] += Gaussian Drift

This simulates real-world changing server conditions.

---

## 📊 Experimental Setup

- Number of servers: 3
- Total requests: 10,000
- Algorithms compared:
  - Random
  - Round-Robin
  - Softmax

Performance metric:
- Average Latency (ms)

---

## ⏱ Time Complexity Analysis

Let:
- N = number of requests
- K = number of servers

Random:
O(N)

Round-Robin:
O(N)

Softmax:
O(NK)

---

## 🚀 How to Run

1. Clone the repository
2. Open in IntelliJ IDEA
3. Run `SmartLoadBalancer.java`
4. Observe average latency results in console

---

## 📈 Expected Result

Softmax achieves lower average latency compared to Random and Round-Robin due to adaptive learning.

---

## 🎥 Demo Video

YouTube (Unlisted): [Add your video link here]

---

## 👩‍💻 Author

Distributed Systems - Final Project
Softmax Based Adaptive Load Balancing
