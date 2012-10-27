desc "Run Spork"
task :spork do
  sh %{bundle exec spork -p 8981}
end
