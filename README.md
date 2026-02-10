# 🚀 Light Furry Games Platform
### Self-Service Multiplayer Infrastructure (Nakama + Agones + Kubernetes)

---

## 🎯 Mission

Enable game developers to spin up **complete multiplayer environments on demand**  
without learning Kubernetes, networking, or scaling.

If you can write a config file → you can run a game backend.

---

## 🧠 What the Platform Automatically Gives You

For every feature or developer environment:

✅ Nakama (auth, realtime, matchmaking)  
✅ CockroachDB  
✅ Prometheus metrics  
✅ Agones game server fleets  
✅ Automatic allocation  
✅ Player → server routing  
✅ Namespace isolation  
✅ Easy cleanup  

---

## 🏗 Architecture Overview

Players connect to Nakama.

When a match is found → Nakama asks Agones for a GameServer.

Agones returns IP/Port → Nakama notifies players.

Players connect to the dedicated server.

---

## 🔄 Runtime Flow

1. Player login  
2. Player joins matchmaker  
3. Match found  
4. Lua runtime calls Agones allocator  
5. Server assigned  
6. Players receive connection info  
7. Game starts 🎉  

---

## 🧩 Stack

| Component | Technology |
|-----------|-----------|
| Container Orchestration | Kubernetes |
| Game Server Scaling | Agones |
| Matchmaking / Gateway | Nakama |
| Database | CockroachDB |
| Metrics | Prometheus |
| Packaging | Helm |
| CI/CD | Jenkins |

---

---

# 👨‍💻 Developer Experience (VERY IMPORTANT)

Developers should not write Kubernetes manifests.

They create **ONE FILE**.

That’s it.

---

## Step 1 – Create a feature folder

```
features/<your-feature>/
```

Example:

```
features/rummy-v2/
```

---

## Step 2 – Add `platform.yaml`

Example:

```yaml
name: rummy-v2
namespace: rummy-v2

fleet:
  replicas: 5
  image: myrepo/rummy-server:latest
  containerPort: 7777

match:
  minPlayers: 2
  maxPlayers: 2
```

---

## Step 3 – Push to Git

Pipeline will handle everything else.

---

---

# 🤖 What CI/CD Does For You

When Jenkins runs:

✅ namespace created  
✅ cockroach installed  
✅ nakama installed  
✅ prometheus installed  
✅ lua scripts mounted  
✅ fleet created  
✅ scaling configured  
✅ services ready  

You receive a working multiplayer backend in minutes.

---

---

# 🧪 How To Test Locally

Port forward Nakama:

```bash
kubectl port-forward -n <namespace> svc/nakama-nucleus 7350:7350
```

Run your client.

When match happens you will receive:

```
CONNECT TO: <gameserver-ip>:<port>
```

---

---

# 📁 Repository Layout

```
light-furry-games-platform/
│
├── nucleus/                 # Base helm chart shared by all environments
│
├── features/
│   ├── feature-a/
│   │   └── platform.yaml
│   ├── feature-b/
│   │   └── platform.yaml
│
├── jenkins/
│   └── pipeline.groovy
│
└── README.md
```

---

---

# 🧼 Destroy Environment

```bash
kubectl delete ns <namespace>
```

Everything is removed.

No leftovers.

---

---

# ⚡ What Agones Handles

- Replaces crashed servers  
- Keeps desired replica count  
- Provides IP/Port allocation  
- Works with dynamic scaling  

---

---

# 🛡 Isolation & Multi-Team Support

Each feature / team gets:

✔ dedicated namespace  
✔ dedicated Nakama  
✔ dedicated DB  
✔ dedicated fleets  

No cross impact.

---

---

# 📈 Why This Platform Matters

Without this:

❌ Devs depend on DevOps  
❌ Manual infra work  
❌ Slow testing  
❌ Hard scaling  

With this:

✅ self-service  
✅ fast iteration  
✅ repeatable  
✅ safe  
✅ production-like  

---

---

# 🚀 Future Enhancements

- Auto environment expiry  
- Web UI for provisioning  
- Cost tracking  
- Global allocation  
- Canary fleets  
- Observability packs  

---

---

# ❤️ Platform Philosophy

Developers should focus on **building games**.

The platform handles infrastructure.

---

