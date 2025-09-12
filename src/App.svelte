<script>

  // Create poll component
  let newPollOptions = [""];

  function addOption() {
    newPollOptions = [...newPollOptions, ""];
  }

  // Vote on existing poll component
  let existingPollNumber = 250;
  let existingPollQuestion = "Pizza Ingredients";
  let existingPollOptions = ["Cheese", "Pepperoni", "Olives", "Mushrooms"];
  let votes = existingPollOptions.map(() => false); // for checkboxes
</script>

<!-- Component 1: create poll -->
<div class="component">
  <h2>Create a new poll</h2>
  <input type="text" placeholder="Enter your poll question">

  {#each newPollOptions as option, i}
    <input type="text" bind:value={newPollOptions[i]} placeholder={`Option ${i + 1}`}>
  {/each}

  <button class="plus" on:click={addOption}>+ Add option</button>
  <button class="primary" type="submit">Create poll</button>
</div>

<div style="height: 50px;"></div>

<!-- Component 2: vote on existing poll -->
<div class="component">
  <h4>Poll #{existingPollNumber}</h4>
  <h3>{existingPollQuestion}</h3>

  <ul>
    {#each existingPollOptions as option, i}
      <li>
        <span>{i + 1}. {option}</span>
        <input type="checkbox" bind:checked={votes[i]}>
      </li>
    {/each}
  </ul>
</div>

<style>
  .component {
    border: 1px solid #ccc;
    padding: 20px;
    margin-bottom: 20px;
    border-radius: 5px;
    width: 400px;
    box-sizing: border-box;
  }
  input[type="text"] {
    width: 100%;
    padding: 5px;
    margin-bottom: 10px;
    display: block;
  }
  button {
    padding: 6px 12px;
    margin-top: 10px;
    margin-right: 5px;
    cursor: pointer;
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: #f5f5f5;
    color: #333;
  }
  button:hover {
    background-color: #e0e0e0;
  }
  ul {
    list-style: none;
    padding-left: 0;
  }
  li {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
</style>
