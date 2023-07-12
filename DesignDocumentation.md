# DesignDocumentation // What tf do I even want to make??/brainstorming

## Soaked
if the player's soaked meter reaches 100% they will not be able to interact at all until 
they get to a drying station that belongs to their team. attacks can add some % to the soaked 
meter if they hit, but if it misses it will instead soak the wall/floor/celling where it hits, this
is useless unless an ability allows the player to used soaked materials nearby.
The goal is to have the most targets on the map, a target belongs to a team when it 
has the most soaked % from that team, a target functions as a player, which can be soaked, 
the only difference is there is no limit to what percent the target can get to.

## BattleLogger
there needs to be a tool to allow admins to review gameplay to see what needs tweaking, this
also needs to provide information about the players playtime and other statistics to give
information about what skill level the players involved are at.

## Water weapons
the intent is to have a shooter game but with some really silly features
players can choose a weapon before the game starts, weapons will be used as your main attack.

### Bubble Gun
use -> shoots a bunch of bubbles in as normal distributed spread around where the player is aiming.

### Water Beam
use -> shoots a single continuous stream of water in the direction the player is aiming.

### Balloon Launcher
use -> fires a single water balloon that deals AOE damage on impact with player or block.

## Classes
players can pick a class before game starts, classes have special abilities.

### Bubbler - damage
main attack -> normal weapon of player's choice

ability 1 (bubble stream) -> dashes forward/backward (depending on if shifting) and launches a stream of bubbles in that direction

ability 2 (orbital bubbles) -> creates bubbles that float around the player, when the player uses the next attack/ability,
one of these bubbles will burst and will add 5% to the power of that attack. if these bubbles are hit by
an opponent they will instead blow up in your face and add 5% to the attack that hit the bubble.

ability 2* (bubble absorb) -> when player has bubbles around them, they can use this ability to absorb these bubbles
and adds 2.5% for each bubble absorbed to their soaked value, this gives a speed boost, which expires when the user uses their next
attack, which will get a 30% increase in power.

### Mirror - gimmick/support
main attack -> normal weapon of player's choice

ability 1 (reflective plating) -> for a short amount of time, any attacks that hit the player will also hit the person who did the attack. 
the time will be decreased every time it successfully reflects a hit.

ability 2 (mirror reflection) -> creates a mirrored clone of yourself for a somewhat brief period of time, this clone will copy 
the user's actions, the mirror plane is the plane which projects to a single vertical line in the center of the user's screen 
based on the user's position snapshotted from the moment this ability is used.

ability *2 (swap reflection) -> user switches position with the active clone, this can be used when the mirror clone is active.

### Towel - support
main attack -> normal weapon of player's choice

ability 1 (towel squeeze) -> absorbs water in nearby area, once this ability picks up any water to a max of 40%, the next use 
will splash the water in a beam in the direction you are facing

ability 2 (absorb) -> dries off a teammate that you are looking at which is in range by some amount, this 
amount will depend on how much water you can absorb and max out at 20% and will move that water onto you.

ability 2* (towel spinner) -> when player is at more than 80% soaked, ability 2 gets replaced with this version
when used, this will spray water in all directions which add up to a total of the player's soaked % while 
absorbing the soaked % from nearby allies. Once used, player using this will be set to 100% soaked.

### Bucket - support

//something ig idk

## Game

### Waiting for players
game waits until set amount of players has joined the game.

players get to select a character class in this phase.

when there are enough players game start a countdown.

when there are >=90% max players game speeds up the countdown.

when countdown ends game starts.

### Game starts
assigns players to teams and teleport players to their team's spawns.

players are allowed to leave their spawn after 3 seconds.

### Game active
checks if there are enough players still playing.
quits out if there isn't.

players try to deal the most damage to the targets on the play area.

game ends when all targets are held by one team for 10 seconds or when time is up.

the winning team is the team that has the most targets at the end of the game.

### Game over
once the game ends, the players will be shown player stats, the winning team, and the damage leaderboard.
