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
      collegeId: "IIT-2024-442",
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

test("Authenticated user: cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob Verma",
      collegeId: "IIT-2023-102",
      isStudentVerified: true,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: can create and query their own rides", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const rideId = "ride_alice_001";

  await assertSucceeds(
    aliceDb.collection("rides").doc(rideId).set({
      userId: ALICE_UID,
      riderId: ALICE_UID,
      riderName: "Alice Sharma",
      pickupLocation: "Hostel Block 4",
      dropLocation: "Central Library",
      fare: 20,
      splitFare: false,
      splitCount: 1,
      otp: "5912",
      status: "requested",
      ecoSavedGrams: 420,
      etaMinutes: 4,
      helmetVerified: true,
      createdAt: new Date(),
    })
  );

  // Client query alignment with where("userId", "==", ALICE_UID)
  await assertSucceeds(
    aliceDb.collection("rides").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: cannot query all rides without userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("rides").get());
});
