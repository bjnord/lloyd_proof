source 'https://rubygems.org'

gem 'rails', '3.2.22.5'
gem 'rake', '0.9.2.2'

gem 'sqlite3'

# Gems used only for assets and not required
# in production environments by default.
group :assets do
  gem 'sass-rails',   '~> 3.2.3'
  gem 'coffee-rails', '~> 3.2.1'
  gem 'therubyracer', '~> 0.12.3', :platforms => :ruby
  gem 'uglifier', '>= 1.0.3'
end

gem 'jquery-rails'

# To use ActiveModel has_secure_password
# gem 'bcrypt-ruby', '~> 3.0.0'

# To use Jbuilder templates for JSON
# gem 'jbuilder'

# Use unicorn as the app server
# gem 'unicorn'

# Deploy with Capistrano
group :development do
  gem 'capistrano'
  # these are needed by net-ssh to support ed25519 keys:
  gem 'rbnacl-libsodium'
  gem 'rbnacl', '~> 4.0.2'  # must be < 5.0
  gem 'bcrypt_pbkdf'  # must be < 2.0
end

# To use debugger
# gem 'debugger'

group :development, :test do
  gem 'rspec-rails'
  gem 'spork'
  gem 'webrat'
  gem 'flog'
  gem 'flay'
  gem 'simplecov', :require => false
  gem 'rails_best_practices'
end
group :test do
  gem 'factory_girl_rails'
  gem 'shoulda-matchers'
  gem 'autotest-rails', '~> 4.2.1', :require => false
  gem 'autotest-fsevent', '~> 0.2.12', :require => false
end
