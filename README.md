# 🚀 Nakama-Agones Matchmaking Nucleus

A high-performance matchmaking and server allocation system bridging **Nakama** and **Agones**. This project supports multi-feature isolation and a self-service developer sandbox model.

## 📖 The Story
This project was built to solve the complexity of routing matched players to dedicated game servers across multiple game modes. By solving the "Lua return-shift" bug in Nakama and implementing strict Agones `selectors`, we've created a system where matchmaking is fast, isolated, and developer-friendly.

---

## 🛠 Developer Self-Service (Sandbox)

Developers can spin up isolated stacks in the **Dev Cluster** to test their specific game server builds and matchmaking logic.

### 1. Create a Bundle
Create a new folder under `/bundles/` (e.g., `/bundles/dev-surendar/`) and add a `config.yaml`. Use the schema below to tune your environment.

### 2. Deployment via Jenkins
The Jenkins pipeline manages the lifecycle of your stack:
1.  **Bundle Name:** Enter your folder name into the Jenkins text field.
2.  **Action:** * `deploy`: Jenkins reads your `config.yaml`, generates your Lua modules via `sed`, and creates your Route53 entry.
    * `destroy`: Completely wipes your stack and DNS records to save costs.

### 3. Connection
Once deployed, your APK/Client can reach your environment at:
`nakama-{{bundle-name}}.dev.game-backend.com`

---

## 🏗 Technical Architecture

### The "Universal" Allocator Template
We use a hardened Lua template that handles the asynchronous return values of Nakama's HTTP client:
```lua
local results = { nk.http_request(url, "POST", headers, body) }
-- Logic inside loops through results to find the HTTP code and Body regardless of order