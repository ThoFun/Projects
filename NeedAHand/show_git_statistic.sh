#!/usr/bin/env bash
  # get all commits of all branches and prepend every commit with the author
  function get_commits_by_author() {
    git shortlog --all --no-merges --numbered --format="%H %f" |
    awk 'BEGIN{author="" ; commits= ""}
         function printAuthorAndCommits(author, commits) {
             gsub(/ *\([0-9]+\)/, "", author); # remove number of commits from author
             print author " " commits
         }
         {
           if ($0 ~ /:$/) {
             printAuthorAndCommits(author, commits)
             commits=""; author=$0
           }
           if ($1 ~ /[a-f0-9]{32}/) {
             commits= commits " " $1
           }
         }
         END{
           printAuthorAndCommits(author, commits)
          }' |
     sed '1d'   # first author change prints empty line
  }
  function show_java_changes_by_author() {
    get_commits_by_author | while read -r line
    do
      author=$(echo $line | awk -F: '{print $1}')
      commits=$(echo $line | awk -F: '{print $2}')
      show_java_changes_by_commits "$author" "$commits"
    done
  }
  function show_java_changes_by_commits() {
    author="$1"
    commits="$2"
    git show --numstat $commits | egrep '^commit |\.java$' |
      awk -v auth="$author" '
        function finishOneCommit(hash) {
          commitHash=$2
          if (filesInCommit > 0) { commitCount++ }
          filesInCommit=0
          # print "finished commit: ", commitHash, " commitCount=", commitCount, " ", $0
        }
        {
           if ($0 ~ /^commit /) {
             finishOneCommit($2);
           } else {
             filesInCommit++;
             cnt++; ins=ins+$1; del=del+$2
           }
        }
        END{
          finishOneCommit("sentinel")
          printf("    %30s: %8d %7d %7d %8d\n", auth, commitCount, ins, del, ins-del)
        }'
  }
  function show() {
    echo "Number of commits with *.java files, inserted and deleted lines in *.java files."
    printf "\n    %30s:  Commits  Insert  Delete  Ins-Del\n" "Author"
    show_java_changes_by_author
    printf "\n\nNumber of commits (java and non-java) by author:\n\n"
    git shortlog --all --no-merges --numbered --summary
  }
  show