# Basic-Repo

## Basic Structure

- **core**: the directory containing the entire distributed system. This is formatted as an IntelliJ project, so opening this directory with IntelliJ is the most natural way of developing.
	- **src**: the source code of the project lives here. Each sub-team has a directory, but as we develop, these directories may need to change to fit the natural grouping of responsibilities. Teams are encouraged to add more directories for organization purposes.
	- **testsrc**: the test code for the project lives here. Because of how Java access control works, you will want your test code for each class to live in a corresponding directory in `testsrc`.
- **sample-game**: the source code for the sample game.
	- **server**: currently empty, but will contain the IntelliJ project for the game server.
	- **client**: currently empty, but will contain the (likely Unity) project for the game client.

## Git Flow

To keep things simple, we will have a simple model for our Git workflow:

- To begin working on changes, **create a branch off of master**. If you are using Git from the Command Line, this will look like:
	- Check you are on `master` by typing `git status`
	- `git checkout -b new-branch-name`
- To work on a branch someone else is already working on:
	- `git checkout --track origin/some-branch-name`
		- **Note:** `origin/` will always be the prefix to the branch name; this exists to indicate that the branch is coming from the project origin, which is defined as the Github clone URL.
- As you are working and want to commit changes to your branch (not `master`!):
	- `git pull`
	- `git commit -a -m "My commit message here"`
	- `git push`
		- **Note:** if Git suggests you run a modified version of this with `--set-origin`, do it.
- Once you think the branch is ready to go into `master`:
	- On Github, navigate to the Pull Requests tab, and click **New pull request**.
	- The base branch should be `master`, and the compare branch should be the branch you are working on.
	- Tap **Create pull request**.
	- **Add at least one reviewer on the PR.** We want to make sure that code is reviewed before it is merged into `master`.
		- Make sure that if another team is affected by your changes, a member of that team is added to the review so that each team knows about the changes.
	- **The review should be approved by a member of the same team, and only then can changes be merged into master.**
-**Note:** Because of this simplified Git workflow, there will be times that `master` does not compile or does not work properly. Given the timeline of this project, there isn't really a good way around this. However, once the project is in a working state, we should avoid merging code into master that breaks the codebase whenever possible.
