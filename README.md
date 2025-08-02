// == FILE: PlayerController.cs ==
using UnityEngine;

[RequireComponent(typeof(CharacterController))]
public class PlayerController : MonoBehaviour
{
    public float speed = 6f;
    private CharacterController controller;
    private Vector3 moveDirection;

    void Start() => controller = GetComponent<CharacterController>();

    void Update()
    {
        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        Vector3 move = new Vector3(h, 0, v).normalized;

        if (move.magnitude > 0.1f)
        {
            float angle = Mathf.Atan2(move.x, move.z) * Mathf.Rad2Deg;
            transform.rotation = Quaternion.Euler(0, angle, 0);
            moveDirection = move * speed;
        }
        else moveDirection = Vector3.zero;

        controller.Move(moveDirection * Time.deltaTime);
    }
}

// == FILE: CarController.cs ==
using UnityEngine;

public class CarController : MonoBehaviour
{
    public float torque = 1000f;
    public float steer = 30f;
    public WheelCollider FL, FR, RL, RR;

    void FixedUpdate()
    {
        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        FL.steerAngle = FR.steerAngle = h * steer;
        FL.motorTorque = FR.motorTorque = v * torque;
    }
}

// == FILE: VehicleEntry.cs ==
using UnityEngine;

public class VehicleEntry : MonoBehaviour
{
    public GameObject player;
    public GameObject car;
    public Transform seat;
    private bool nearCar;
    private bool inCar;

    void Update()
    {
        if (nearCar && Input.GetKeyDown(KeyCode.E))
        {
            inCar = !inCar;
            player.SetActive(!inCar);
            car.GetComponent<CarController>().enabled = inCar;
            if (inCar) car.transform.position = seat.position;
        }
    }

    void OnTriggerEnter(Collider other)
    {
        if (other.CompareTag("Car")) nearCar = true;
    }

    void OnTriggerExit(Collider other)
    {
        if (other.CompareTag("Car")) nearCar = false;
    }
}

// == FILE: NPCWander.cs ==
using UnityEngine;
using UnityEngine.AI;

public class NPCWander : MonoBehaviour
{
    public float wanderRadius = 10f;
    public float wanderTimer = 5f;
    private NavMeshAgent agent;
    private float timer;

    void OnEnable()
    {
        agent = GetComponent<NavMeshAgent>();
        timer = wanderTimer;
    }

    void Update()
    {
        timer += Time.deltaTime;
        if (timer >= wanderTimer)
        {
            Vector3 newPos = RandomNavSphere(transform.position, wanderRadius, -1);
            agent.SetDestination(newPos);
            timer = 0;
        }
    }

    static Vector3 RandomNavSphere(Vector3 origin, float dist, int layermask)
    {
        Vector3 rand = Random.insideUnitSphere * dist;
        NavMesh.SamplePosition(origin + rand, out NavMeshHit hit, dist, layermask);
        return hit.position;
    }
}

// == FILE: WantedSystem.cs ==
using UnityEngine;
using UnityEngine.UI;

public class WantedSystem : MonoBehaviour
{
    public int wantedLevel = 0;
    public Text wantedText;

    void Update()
    {
        wantedText.text = "Wanted: " + wantedLevel;
    }

    public void IncreaseWanted() => wantedLevel = Mathf.Min(5, wantedLevel + 1);
}

// == FILE: GunSystem.cs ==
using UnityEngine;

public class GunSystem : MonoBehaviour
{
    public GameObject bulletPrefab;
    public Transform firePoint;
    public float fireRate = 0.5f;
    private float fireTimer;

    void Update()
    {
        fireTimer += Time.deltaTime;
        if (Input.GetButton("Fire1") && fireTimer > fireRate)
        {
            Instantiate(bulletPrefab, firePoint.position, firePoint.rotation);
            fireTimer = 0;
        }
    }
}

// == FILE: MissionTrigger.cs ==
using UnityEngine;

public class MissionTrigger : MonoBehaviour
{
    public GameObject missionUI;

    void OnTriggerEnter(Collider other)
    {
        if (other.CompareTag("Player")) missionUI.SetActive(true);
    }

    void OnTriggerExit(Collider other)
    {
        if (other.CompareTag("Player")) missionUI.SetActive(false);
    }
}

// == FILE: UIManager.cs ==
using UnityEngine;
using UnityEngine.UI;

public class UIManager : MonoBehaviour
{
    public Text healthText, moneyText;
    public int health = 100, money = 0;

    void Update()
    {
        healthText.text = "Health: " + health;
        moneyText.text = "$" + money;
    }

    public void AddMoney(int amount) => money += amount;
    public void Damage(int dmg) => health = Mathf.Max(0, health - dmg);
}

// == FILE: .gitignore ==
[Ll]ibrary/
[Tt]emp/
[Oo]bj/
[Bb]uild/
[Bb]uilds/
[Ll]ogs/
[Mm]emoryCaptures/
*.csproj
*.unityproj
*.sln
*.user
*.userprefs
*.pidb
*.booproj
*.svd
*.pdb
*.mdb
*.opendb
*.VC.db
.DS_Store
*.apk
*.aab

// == FILE: README.md ==
# 🎮 GTA-Style Unity Game

A mini open-world third-person action game built with Unity and C#. GTA-style functionality.

## ✅ Features
- TPS Player + Car Entry/Exit
- Driveable Cars
- NPC AI Wanderers
- Gun System (basic shooting)
- Wanted Level Indicator
- Simple Mission Trigger
- UI (Health + Money)

## 🛠 Setup
1. Open in Unity
2. Place scripts in `Assets/Scripts/`
3. Assign player, car, NPC prefabs
4. Setup NavMesh for NPCs
5. Create UI Texts for health, money, wanted level

## 📦 To Do
- Save/load game
- Cops & chasing logic
- Weapon pickups
- Real missions with objectives