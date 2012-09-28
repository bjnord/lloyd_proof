desc "Run Spork"
task :spork do
  sh %{bundle exec spork}
end
