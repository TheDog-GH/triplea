# IntelliJ errors how-to
## cannot find symbol class ... ([IDEA-267526](https://youtrack.jetbrains.com/issue/IDEA-267526))
**Symptom**

![RunError](https://user-images.githubusercontent.com/10353640/153857997-f28ab5ed-4f36-4d05-80eb-df45702dc1f4.png)
![RunErrorDetail](https://user-images.githubusercontent.com/10353640/153858470-bcf2bc2c-f980-4035-be74-c9f5a27bfc97.png)

**Potential steps to fix (update if step-by-step process is found)**
1. In tab *Gradle* perform gradle task *Tasks > build > clean*
2. In tab *Gradle* perform gradle task *Tasks > build > build*

![gradleTaskBuild](https://user-images.githubusercontent.com/10353640/153848041-450573f3-d873-4ebd-96d8-bd56829ad382.png)

2. In tab *Git* checkout different branch, e.g., master
3. Delete local branch that the error occurrs in (make sure all changes are pushed to github)
4. Choose menu entry *File > Invalidate Caches...* and confirm with button *Invalidate and Restart*

![invalidateAndRestart](https://user-images.githubusercontent.com/10353640/153858793-a97cd147-cb07-4db1-8a82-de97547b4564.png)

5. Perform commit directly on github
6. Fetch from remote in IntelliJ tab *Git*
