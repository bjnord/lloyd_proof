# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]
# 
# == Description
#
# This mix-in for ActiveRecord models adds substitute persistence methods
# which, instead of returning a boolean or an object, return a status hash
# suitable _e.g._ for rendering as JSON.
#
# === Return format examples
#
#   {:sync_id => 17, :status => :ok}
#   {:sync_id => 21, :status => :error, :errors => ["Current text cannot be blank"]}

module ReturnStatus
  class StatusFormatter  #:nodoc: internal use
    attr_reader :status

    def initialize(sync_id)
      @status = {:sync_id => sync_id, :status => :ok}
    end

    def errors=(errors)
      @status.merge!({:status => :error, :errors => errors})
    end
  end

  # <http://stackoverflow.com/questions/3357712/static-methods-in-ruby-modules>
  def self.included(o)  #:nodoc:
    o.extend(ClassMethods)
  end

  # Like ActiveRecord #save, but returns a status hash instead of a boolean.
  def save_and_return_status(sync_id)
    formatter = StatusFormatter.new(sync_id)
    formatter.errors = self.errors.full_messages unless self.save
    formatter.status
  end

  module ClassMethods
    # Like ActiveRecord #create, but returns a status hash instead of a
    # boolean. The attributes hash should include a +sync_id+ attribute,
    # which will not be saved, but will be returned in the status hash.
    #
    # (Note that this method gives you no way to identify the created
    # object; it should be used in "save and forget" contexts.)
    def create_and_return_status(attr)
      formatter = StatusFormatter.new(attr.delete(:sync_id))
      begin
        corr = Correction.create(attr)
        formatter.errors = corr.errors.full_messages unless corr.persisted?
      rescue ActiveModel::MassAssignmentSecurity::Error => e
        formatter.errors = [e.message]
      end
      formatter.status
    end
  end
end
