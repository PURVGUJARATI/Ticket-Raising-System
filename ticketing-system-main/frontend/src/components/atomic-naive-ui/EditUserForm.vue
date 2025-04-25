<script setup>
import { ref, onMounted, computed } from "vue";
import { NButton, NInput, NCard, NDivider, NFormItem, NCheckbox } from "naive-ui";
import { storeToRefs } from "pinia";
import { useUserStore } from "../../stores/user";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);

const firstCheckBox = ref(false);
const secondCheckBox = ref(false);
const thirdCheckBox = ref(false);
const fourthCheckBox = ref(false);
const fithCheckBox = ref(false);
const sixthCheckBox = ref(false);
const seventhCheckBox = ref(false);
const eighthCheckBox = ref(false);
const ninthCheckBox = ref(false);
const tenthCheckBox = ref(false);
const eleventhCheckBox = ref(false);
const twelfthCheckBox = ref(false);
const isDeleteUserButtonDisabled = computed(() => 
  !firstCheckBox.value || !secondCheckBox.value || !thirdCheckBox.value 
  //!fourthCheckBox.value || !fithCheckBox.value || !sixthCheckBox.value ||
  //!seventhCheckBox.value || !eighthCheckBox.value || !ninthCheckBox.value ||
  //!tenthCheckBox.value || !eleventhCheckBox.value || !twelfthCheckBox.value
);

const isNameSubmitButtonDisabled = computed(() => user.value.name == undefined || user.value.name.length == 0);
const isEmailSubmitButtonDisabled = true; // computed(() => user.value.email == undefined || user.value.email.length == 0);

async function handleSubmitUserName() {
  const response = await userStore.patchUserById(user.value.id, { name: user.value.name, email: null });
  if (!response.isSuccessful) {
    console.log(response.data)
  }
}
async function handleSubmitNewUserEmail() {
  const response = await userStore.patchUserById(user.value.id, { name: null, email: user.value.email });
  if (!response.isSuccessful) {
    console.log(response.data)
  }
}
async function handleDeleteUser() {
  const response = await userStore.deleteUserById(user.value.id);
  if (!response.isSuccessful) {
    console.log(response.data)
  }
}

onMounted(async () => {
  await userStore.updateUserById();
});
</script>

<template>
  <n-card style="width: 30%; min-width: 500px; background-color: #000; border-radius: 5px;" title="Edit Profile"
    :bordered="false" size="huge" role="dialog" aria-modal="true">
    <div>

      <div style="display: flex; font-size: 1.2em; color: #fff;">
        <div style="width: 95%;">
          <n-form-item label="Name" label-style="color: #fff;">
            <n-input v-model:value="user.name" style="background-color: #333; color: #fff; border-color: #444; border-radius: 5px;" />
            <n-button @click="handleSubmitUserName" :disabled="isNameSubmitButtonDisabled" block secondary strong
              style="max-width: 70px; margin-left: 10px; border-radius: 5px; background-color: #1e90ff; color: #fff; box-shadow: 2px 2px 3px #222;">
              Change
            </n-button>
          </n-form-item>
        </div>
      </div>

      <div style="display: flex; font-size: 1.2em; color: #fff;">
        <div style="width: 95%;">
          <n-form-item label="E-Mail (updating your E-Mail is not supported at the moment)" label-style="color: #fff;">
            <n-input :disabled="true" type="email" v-model:value="user.email" style="background-color: #333; color: #fff; border-color: #444; border-radius: 5px;" />
            <n-button @click="handleSubmitNewUserEmail" :disabled="isEmailSubmitButtonDisabled" block secondary strong
              style="max-width: 70px; margin-left: 10px; border-radius: 5px; background-color: #1e90ff; color: #fff; box-shadow: 2px 2px 3px #222;">
              Change
            </n-button>
          </n-form-item>
        </div>
      </div>

      <n-divider style="margin-top: 0px; background-color: #444;" />

      <div>
        <n-checkbox v-model:checked="firstCheckBox" style="color: #fff;">
          Yes, I want to delete my profile.
        </n-checkbox>
      </div>
      <div v-if="firstCheckBox">
        <br/>
        <n-checkbox v-model:checked="secondCheckBox" style="color: #fff;">
          I understand that projects, their tickets and open invitations get deleted when the last project member leaves.
        </n-checkbox>
      </div>
      <div v-if="secondCheckBox">
        <br/>
        <n-checkbox v-model:checked="thirdCheckBox" style="color: #fff;">
          <div>
            <div>
              Deleted data is gone forever and cannot be recovered.
            </div>
            <div>
              Do you understand?
            </div>
          </div>
        </n-checkbox>
      </div>
      <div v-if="fourthCheckBox">
        <br/>
        <n-checkbox v-model:checked="fourthCheckBox" style="color: #fff;">
          Are you really sure?
        </n-checkbox>
      </div>
      <div v-if="fourthCheckBox">
        <br/>
        <n-checkbox v-model:checked="fithCheckBox" style="color: #fff;">
          You may never find a ticketing system as good as this again.
        </n-checkbox>
      </div>
      <div v-if="fithCheckBox">
        <br/>
        <n-checkbox v-model:checked="sixthCheckBox" style="color: #fff;">
          I mean, it is THE Ticketing System!
        </n-checkbox>
      </div>
      <div v-if="sixthCheckBox">
        <br/>
        <n-checkbox v-model:checked="seventhCheckBox" style="color: #fff;">
          Never gonna give you up, never gonna let you down!
        </n-checkbox>
      </div>
      <div v-if="seventhCheckBox">
        <br/>
        <n-checkbox v-model:checked="eighthCheckBox" style="color: #fff;">
          You know, I can do this all day. This isn't my first rodeo.
        </n-checkbox>
      </div>
      <div v-if="eighthCheckBox">
        <br/>
        <n-checkbox v-model:checked="ninthCheckBox" style="color: #fff;">
          Never gonna run around and desert you!
        </n-checkbox>
      </div>
      <div v-if="ninthCheckBox">
        <br/>
        <n-checkbox v-model:checked="tenthCheckBox" style="color: #fff;">
          Never gonna make you cry, NEVER GONNA SAY GOODBYEEE!!!
        </n-checkbox>
      </div>
      <div v-if="tenthCheckBox">
        <br/>
        <n-checkbox v-model:checked="eleventhCheckBox" style="color: #fff;">
          Hello this is Michael Jackson! Can you send me 6$ via PayPal so I can make music again? Hee hee!
        </n-checkbox>
      </div>
      <div v-if="eleventhCheckBox">
        <br/>
        <n-checkbox v-model:checked="twelfthCheckBox" style="color: #fff;">
          <div>
            <div>
              All right, all right, you earned it. I hope you had a good user experience. 
              You are probably the only user anyway. 
            </div>
            <div>
              Farewell, and don't forget: The cake is a lie!
            </div>
          </div>
        </n-checkbox>
      </div>

      <br/>
      <br/>

      <div style="display: flex; justify-content: center;">
        <n-button @click="handleDeleteUser" :disabled="isDeleteUserButtonDisabled" type="error" block error strong
          style="max-width: 180px; margin-left: 10px; border-radius: 5px; background-color: #ff4d4f; color: #fff; box-shadow: 2px 2px 3px #222;">
          Delete my profile
        </n-button>
      </div>

    </div>
  </n-card>
</template>

<style scoped>
/* Custom dark theme adjustments */
.n-card {
  --n-card-color: #000 !important;
  --n-title-text-color:#63e2b7 !important;
}

.n-input {
  --n-border-color: #444 !important;
  --n-color: #333 !important;
  --n-text-color: #fff !important;
}

.n-button {
  --n-color: #1e90ff !important;
  --n-text-color: #fff !important;
}

.n-button[type="error"] {
  --n-color: #ff4d4f !important;
}

.n-divider {
  --n-color: #444 !important;
}
:deep(.n-checkbox .n-checkbox__label) {
    color: rgb(216 223 230);
    transition: color .3s var(--n-bezier);
    user-select: none;
    -webkit-user-select: none;
    padding: var(--n-label-padding);
}
</style>