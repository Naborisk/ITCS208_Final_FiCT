// Tussoun Jitpanyoyos #6088030 Section 1

public class Player {
	/*
	* TO-DO List
	* - Attack front first - completed
	* - implement special attack - completed
	*
	* - find a better way to implement i and j - completed
	* */

	public enum PlayerType {Healer, Tank, Samurai, BlackMage, Phoenix, Cherry};
	
	private PlayerType type; 	//Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix
	private double maxHP;		//Max HP of this player
	private double currentHP;	//Current HP of this player 
	private double atk;			//Attack power of this player
	private int numSpAtk;       //Number of turns before special attack
	private boolean isCursed, isSleeping, isTaunting;
	private String spAtkAction; //Action of the special attack (Verb form)
	private int posI;
	private int posJ;
	private Player cursedTarget = null; //Target that a BlackMage have cursed
	private Player revivedPlayer = null;
	private Player healedPlayer = null;
	private int internalClock;
	
	
	/**
	 * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns, 
	 * as specified in the given table. It also reset the internal turn count of this player. 
	 * @param _type
	 */
	public Player(PlayerType _type) {
		//INSERT YOUR CODE HERE
		type = _type;

		switch(_type){
			case Healer:
				maxHP = 4790;
				atk = 238;
				numSpAtk = 4;
				spAtkAction = "Heals";
			break;
			case Tank:
				maxHP = 5340;
				atk = 255;
				numSpAtk = 4;
				spAtkAction = "is Taunting";
			break;
			case Samurai:
				maxHP = 4005;
				atk = 368;
				numSpAtk = 3;
				spAtkAction = "Double-Slashes";
			break;
			case BlackMage:
				maxHP = 4175;
				atk = 303;
				numSpAtk = 4;
				spAtkAction = "Curses";
			break;
			case Phoenix:
				maxHP = 4175;
				atk = 209;
				numSpAtk = 8;
				spAtkAction = "Revives";
			break;
			case Cherry:
				maxHP = 3560;
				atk = 198;
				numSpAtk = 4;
				spAtkAction = "Feeds a Fortune Cookie to";
			break;
		}

		currentHP = maxHP;
		isCursed = false;
		isSleeping = false;
		isTaunting = false;
		internalClock = 1;

	}

	/**
	 * Returns the current HP of this player
	 * @return
	 */
	public double getCurrentHP() {
		return currentHP;
	}
	
	/**
	 * Returns type of this player
	 * @return
	 */
	public Player.PlayerType getType() {
		return type;
	}

	/**
	 * Returns max HP of this player. 
	 * @return
	 */
	public double getMaxHP() {
		return maxHP;
	}
	
	/**
	 * Returns whether this player is sleeping.
	 * @return
	 */
	public boolean isSleeping() {
		return isSleeping;
	}
	
	/**
	 * Returns whether this player is being cursed.
	 * @return
	 */
	public boolean isCursed() {
		return isCursed;
	}
	
	/**
	 * Returns whether this player is alive (i.e. current HP > 0).
	 * @return
	 */
	public boolean isAlive() {
		if(currentHP > 0) return true;
		else return false;
	}
	
	/**
	 * Returns whether this player is taunting the other team.
	 * @return
	 */
	public boolean isTaunting() {
		return isTaunting;
	}
	
	public void attack(Player target) {
		target.currentHP -= atk;
		if(target.currentHP < 0){
			target.currentHP = 0;
			target.isTaunting = false;
			target.isCursed = false;
			target.isSleeping = false;
		}
	}

	public void useSpecialAbility(Player[][] myTeam, Player[][] theirTeam) {
		Player target = getLowestCurrentHp(theirTeam, true);

		switch(type){
			//Heals ally
			case Healer:
				if(getLowestPercentageHp(myTeam) != null) heal(getLowestPercentageHp(myTeam));
			break;
			//Taunts for a turn
			case Tank:
				isTaunting = true;
			break;
			case Samurai:
				attack(target);
				attack(target);
			break;
			case BlackMage:
				if(target.isAlive()) {
					target.isCursed = true;
					cursedTarget = target;
				}
			break;
			//Revives
			case Phoenix:
				findAndRevive(myTeam);
			break;
			case Cherry:
				for(int i = 0; i < theirTeam.length; i++){
					for(int j = 0; j < theirTeam[0].length; j++){
						if(theirTeam[i][j].isAlive()){
							theirTeam[i][j].isSleeping = true;
						}
					}
				}
			break;
		}
	}

	private void findAndRevive(Player[][] team){

		for(int i = 0; i < team.length; i++){
			for(int j = 0; j< team[0].length; j++){
				if(!team[i][j].isAlive()) {
					team[i][j].currentHP = team[i][j].getMaxHP() * 0.3;
					team[i][j].internalClock = 1;
					revivedPlayer = team[i][j];
					return;
				}
			}
		}
	}

	private Player playerToRevive(Player[][] team){

		for(int i = 0; i < team.length; i++){
			for(int j = 0; j< team[0].length; j++){
				if(!team[i][j].isAlive()) {
					return team[i][j];
				}
			}
		}
		return team[0][0];
	}

	private void heal(Player playerToHeal){

		playerToHeal.currentHP += playerToHeal.maxHP * 25 / 100;
		if(playerToHeal.currentHP >= playerToHeal.maxHP) playerToHeal.currentHP = playerToHeal.maxHP;
	}

	private void curse(Player[][] team){

		Player playerToCurse = getLowestCurrentHp(team, true);
		playerToCurse.isCursed = true;
	}

	protected void setPos(int i, int j){
		this.posI = i;
		this.posJ = j;
	}

	//Find player w/ lowest HP on a team, battleMode will prioritize front and check for taunt
	public Player getLowestCurrentHp(Player[][] team, boolean battleMode){

		Player playerLowestHp = team[0][0];

		double minHp = 99999.0;

		for(int i = 0; i < team.length; i++){
			for(int j = 0; j < team[0].length; j++){
				if(team[i][j].getCurrentHP() < minHp && team[i][j].isAlive()){
					minHp = team[i][j].getCurrentHP();
					playerLowestHp = team[i][j];
				}

				// return if a player isTaunting and is checking in battle mode
				if(team[i][j].isTaunting && battleMode) return team[i][j];
			}
			if(Arena.frontRemaining(team) != 0 && battleMode) break;
		}

		for(int i = 0; i < team.length; i++){
			for(int j = 0; j < team[0].length; j++){
				if(team[i][j].isTaunting && battleMode) return team[i][j];
			}
		}


		return playerLowestHp;
	}

	private Player getLowestPercentageHp(Player[][] team){
		Player playerLowestHp = team[0][0];
		double minHpPercent = 99999.0;

		for(int i = 0; i < team.length; i++){
			for(int j = 0; j < team[0].length; j++){
				if(team[i][j].getCurrentHP()/team[i][j].getMaxHP() < minHpPercent && team[i][j].isAlive() && !isCursed){
					minHpPercent = team[i][j].getCurrentHP()/team[i][j].getMaxHP();
					playerLowestHp = team[i][j];
					healedPlayer = team[i][j];
				}
			}
		}

		if (!playerLowestHp.isAlive() || (playerLowestHp.getCurrentHP() == playerLowestHp.getMaxHP())) return null;
		return playerLowestHp;
	}


	//log example
	//
	//#   v row of this | v type of this
	//# A[Front][1] {Samurai} Attacks B[Front][2] {Samurai}
	//# ^ team of this
	//           ^ position of this

	private void logPlayerAction(Player self, String action,Player target, Arena arena){

		String myRow = (self.posI == 0) ? "Front" : "Back";
		String targetRow = (target.posI == 0) ? "Front" : "Back";

		char myTeamChar = (arena.isMemberOf(self, Arena.Team.A)) ? 'A' : 'B';
		char targetTeamChar =(arena.isMemberOf(target, Arena.Team.A)) ? 'A' : 'B';

		System.out.println("# " + myTeamChar + "[" + myRow + "][" + (self.posJ+1) + "] {" + self.getType() + "} "
				+ action
				+ " " + targetTeamChar + "[" + targetRow + "][" + (target.posJ+1) + "] {" + target.getType() + "}");
	}

	private void logTaunting(Arena arena){

		String myRow = (this.posI == 0) ? "Front" : "Back";

		char myTeamChar = (arena.isMemberOf(this, Arena.Team.A)) ? 'A' : 'B';

		System.out.println("# " + myTeamChar + "[" + myRow + "][" + (this.posJ+1) + "] {" + this.getType() + "} "
				+ spAtkAction);
	}

	
	/**
	 * This method is called by Arena when it is this player's turn to take an action. 
	 * By default, the player simply just "attack(target)". However, once this player has 
	 * fought for "numSpecialTurns" rounds, this player must perform "useSpecialAbility(myTeam, theirTeam)"
	 * where each player type performs his own special move. 
	 * @param arena
	 */

	// code sample
	//teamA[i][j].takeAction(this);

	public void takeAction(Arena arena) {

		//clear statuses
		isTaunting = false;
		if(cursedTarget != null){
			cursedTarget.isCursed = false;
			cursedTarget = null;
		}
		if(isSleeping){
			isSleeping = false;
			return;
		}

		Player[][] teamA = arena.getTeamA();
		Player[][] teamB = arena.getTeamB();

		Player[][] myTeam = (arena.isMemberOf(this, Arena.Team.A)) ? teamA : teamB;
		Player[][] theirTeam = (arena.isMemberOf(this, Arena.Team.A)) ? teamB : teamA;

		Player attackTarget;

		boolean willUseSpecialAbility = false;
		boolean willAttack = true;

		// Init the object
		if(arena.isMemberOf(this, Arena.Team.A)) attackTarget = teamB[0][0];
		else attackTarget = teamA[0][0];

		// Determines if this round will be a special attack
		// arena.getNumRounds()+1 can't be used in an if statement due to the confusion with % and +
		//int currentRound = arena.getNumRounds()+1;
		if(internalClock % numSpAtk == 0){
			willUseSpecialAbility = true;
		}

		// Check the team of player who takes action, then performs an action to the opposing team's player accordingly
		if(arena.isMemberOf(this, Arena.Team.A)){
			attackTarget = getLowestCurrentHp(teamB, true);
		}else{
			attackTarget = getLowestCurrentHp(teamA, true);
		}


		//set the boolean of weather the player will attack
		if(arena.playersLeft((arena.isMemberOf(this, Arena.Team.A)) ? Arena.Team.B: Arena.Team.A ) == 0) willAttack = false;

		//it would be better to log right before performing the action since the target in log may change after the action
		//not to mention this way would make the code looks cleaner
		if(this.isAlive() && !willUseSpecialAbility){
			if(willAttack) logPlayerAction(this, "Attacks", attackTarget, arena);
			attack(attackTarget);
		}else if(this.isAlive() && willUseSpecialAbility){
			switch (type){
				case Healer:
					if(getLowestPercentageHp(myTeam) != null) logPlayerAction(this, spAtkAction, getLowestPercentageHp(myTeam), arena);
				break;
				case Tank:
					logTaunting(arena);
				break;
				case Samurai:
					if(willAttack) logPlayerAction(this, spAtkAction, attackTarget, arena);
				break;
				case BlackMage:
					if(willAttack) logPlayerAction(this, spAtkAction, attackTarget, arena);
				break;
				case Phoenix:
					logPlayerAction(this, spAtkAction, playerToRevive(myTeam), arena);
				break;
				case Cherry:
					for(int i = 0; i < myTeam.length; i++){
						for(int j = 0; j < myTeam[0].length; j++){
							if(theirTeam[i][j].isAlive()) {
								logPlayerAction(this, spAtkAction, theirTeam[i][j],arena);
							}
						}
					}
				break;
			}

			if(arena.isMemberOf(this, Arena.Team.A)) useSpecialAbility(teamA, teamB);
			else useSpecialAbility(teamB, teamA);
		}

		revivedPlayer = null;
		healedPlayer = null;
		internalClock++;
	}
	
	/**
	 * This method overrides the default Object's toString() and is already implemented for you. 
	 */
	@Override
	public String toString()
	{
		return "["+this.type.toString()+" HP:"+this.currentHP+"/"+this.maxHP+" ATK:"+this.atk+"]["
				+((this.isCursed())?"C":"")
				+((this.isTaunting())?"T":"")
				+((this.isSleeping())?"S":"")
				+"]";
	}
	
	
}
