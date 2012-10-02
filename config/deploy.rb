require "bundler/capistrano"

set :application, "lloyd_proof"

set :repository, "git.nordist.net:/export/git/lloyd_proof.git"
set :scm, :git

server "git.nordist.net", :app, :web, :db, :primary => true
set :deploy_to, "/export/app/lloydproof"

set :use_sudo, false
set :ssh_options, { :forward_agent => true }
default_run_options[:pty] = true

# <https://github.com/capistrano/capistrano/issues/79>
set :normalize_asset_timestamps, false

after "deploy:restart", "deploy:cleanup"
set :keep_releases, 12

# keep files/directories read-only to the web server, for better security
set :group_writable, false
namespace :bundle do
  task :no_group_writable do
    run "#{try_sudo} chmod -R go-w #{shared_path}/bundle"
  end
end
after "bundle:install", "bundle:no_group_writable"

# If you are using Passenger mod_rails uncomment this:
namespace :deploy do
  task :start do ; end
  task :stop do ; end
  task :restart, :roles => :app, :except => { :no_release => true } do
    run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
  end
end
