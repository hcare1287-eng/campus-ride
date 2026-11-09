const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read rides or users", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("rides").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create own profile and read it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Sharma",
      collegeId: "DBU/2024/ENG/108",
      isStudentVerified: true,
      rating: 4.9,
      ridesCount: 12,
      ecoPoints: 340,
      role: "both",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create a ride and another peer driver can discover and accept it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const rideId = "ride_alice_001";
  const now = new Date();

  // 1. Alice creates ride
  await assertSucceeds(
    aliceDb.collection("rides").doc(rideId).set({
      userId: ALICE_UID,
      riderId: ALICE_UID,
      riderName: "Alice Sharma",
      pickupLocation: "S. Lal Singh Boys Hostel",
      dropLocation: "DBU Central Library (Kalam Block)",
      fare: 20,
      splitFare: false,
      splitCount: 1,
      otp: "5912",
      status: "requested",
      ecoSavedGrams: 420,
      etaMinutes: 4,
      helmetVerified: true,
      createdAt: now,
    })
  );

  // 2. Bob (driver) discovers open requests
  await assertSucceeds(
    bobDb.collection("rides").where("status", "==", "requested").get()
  );

  // 3. Bob claims the ride
  await assertSucceeds(
    bobDb.collection("rides").doc(rideId).update({
      driverId: BOB_UID,
      driverName: "Bob Singh",
      vehiclePlate: "PB-23-EV-4412",
      status: "accepted",
      etaMinutes: 3,
    })
  );
});

test("Authenticated user: cannot query all rides arbitrarily without status or userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("rides").get());
});

test("Authenticated user: can create SOS emergency alert", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("emergency_alerts").doc("alert_001").set({
      userId: ALICE_UID,
      studentName: "Alice Sharma",
      collegeId: "DBU/2024/ENG/108",
      latitude: 30.6062,
      longitude: 76.3117,
      nearestLandmark: "DBU Central Library",
      status: "ACTIVE",
      createdAt: new Date(),
    })
  );
});
